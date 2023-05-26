package com.cda.service.tenant;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_DRIVER;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_URL;
import static org.hibernate.cfg.AvailableSettings.PASS;
import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.USER;
import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_REFLECTION_OPTIMIZER;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;

import com.cda.configuration.ApplicationProperties;
import com.cda.entities.Tenant;
import com.cda.exceptions.TenantContextException;
import com.cda.exceptions.TenantCreationException;
import com.cda.model.TenantInputModel;
import com.cda.persistence.PersistenceUnitInfoImpl;
import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;
import com.cda.repository.tenant.TenantRepository;
import com.cda.service.BaseService;
import com.cda.utils.EncryptionService;
import com.cda.utils.JdbcDriver;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;

public class TenantService extends BaseService {
  private final EncryptionService encryptionService;

  private final ApplicationProperties properties;

  public TenantService(
      RepositoryFactory repositoryFactory,
      TransactionHandler transactionHandler,
      EncryptionService encryptionService,
      ApplicationProperties properties) {
    super(repositoryFactory, transactionHandler);
    this.encryptionService = encryptionService;
    this.properties = properties;
  }

  private static final String VALID_DATABASE_NAME_REGEXP = "[A-Za-z0-9_]*";

  public EntityManagerFactory createEntityManagerFactory(Tenant tenant, File jarFile) {
    try {
      URL url = new URL(jarFile.getPath());
      URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {url});

      JdbcDriver dbmsType = JdbcDriver.valueOf(properties.getDbmsName());
      PersistenceUnitInfo persistenceUnitInfo =
          new PersistenceUnitInfoImpl(tenant.getId(), tenant.getPackageName(), urlClassLoader);
      return new HibernatePersistenceProvider()
          .createContainerEntityManagerFactory(
              persistenceUnitInfo,
              ImmutableMap.<String, Object>builder()
                  .put(JPA_JDBC_DRIVER, dbmsType.getDriverClassName())
                  .put(JPA_JDBC_URL, properties.getTenantUrlPrefix() + tenant.getDb())
                  .put(USER, tenant.getId())
                  .put(
                      PASS,
                      encryptionService.decrypt(
                          tenant.getPassword(), properties.getSecret(), properties.getSalt()))
                  .put(DIALECT, MySQL5Dialect.class)
                  .put(HBM2DDL_AUTO, "update")
                  .put(SHOW_SQL, true)
                  .put(QUERY_STARTUP_CHECKING, false)
                  .put(GENERATE_STATISTICS, false)
                  .put(USE_REFLECTION_OPTIMIZER, false)
                  .put(USE_SECOND_LEVEL_CACHE, false)
                  .put(USE_QUERY_CACHE, false)
                  .put(USE_STRUCTURED_CACHE, false)
                  .put(STATEMENT_BATCH_SIZE, 25)
                  .build());
    } catch (Exception e) {
      throw new TenantContextException(
          "Error while creating connection for tenant \"" + tenant.getId() + "\"", e);
    }
  }

  public void create(TenantInputModel model) throws Exception {
    transactionHandler.encapsulateTransaction(
        () -> {
          String tenantId = model.getName();
          String password = model.getPassword();
          String db = "tenant_" + tenantId;

          TenantRepository tenantRepository = repositoryFactory.buildTenantRepository();
          Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
          if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();
            tenant.setPackageName(model.getPackageName());

            return tenantRepository.save(tenant);
          } else {

            // Verify db string to prevent SQL injection
            if (!db.matches(VALID_DATABASE_NAME_REGEXP)) {
              throw new TenantCreationException("Invalid db name: " + db);
            }

            String url = properties.getTenantUrlPrefix() + db;
            String encryptedPassword =
                encryptionService.encrypt(password, properties.getSecret(), properties.getSalt());

            Tenant tenant =
                new Tenant(tenantId, db, encryptedPassword, url, model.getPackageName());
            tenant = tenantRepository.save(tenant);
            createDatabase(db, password, tenantId);
            return tenant;
          }
        });
  }

  private Connection createMasterConnection() throws SQLException {
    return DriverManager.getConnection(
        properties.getMasterUrl(), properties.getMasterUser(), properties.getMasterPassword());
  }

  private void createDatabase(String db, String password, String tenantId) {
    try (Connection connection = createMasterConnection()) {
      // Create database
      String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + db;
      try (Statement statement = connection.createStatement()) {
        statement.execute(createDatabaseSql);
      }

      String dropUserSql = "DROP USER IF EXISTS " + tenantId;
      try (Statement statement = connection.createStatement()) {
        statement.execute(dropUserSql);
      }

      // Create user
      String createUserSql = "CREATE USER " + tenantId + " IDENTIFIED BY '" + password + "'";
      try (Statement statement = connection.createStatement()) {
        statement.execute(createUserSql);
      }

      // Grant privileges to user
      String grantPrivilegesSql = "GRANT ALL PRIVILEGES ON " + db + " TO " + tenantId;
      try (Statement statement = connection.createStatement()) {
        statement.execute(grantPrivilegesSql);
      }

      System.out.println("Database and user created successfully.");
    } catch (Exception e) {
      throw new TenantCreationException(
          "Error while creating credentials for tenant '" + tenantId + "'", e);
    }
  }
}
