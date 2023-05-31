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
import com.cda.exceptions.TenantRegistryException;
import com.cda.model.TenantInputModel;
import com.cda.model.TenantQueryInputModel;
import com.cda.multitenant.TenantEntityManagerFactoryContext;
import com.cda.persistence.PersistenceUnitInfoImpl;
import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;
import com.cda.repository.tenant.TenantRepository;
import com.cda.service.BaseService;
import com.cda.utils.EncryptionService;
import com.cda.utils.JdbcDriver;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitInfo;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.util.CollectionUtils;

public class TenantServiceImpl extends BaseService implements TenantService {
  private final EncryptionService encryptionService;
  private final TenantEntityManagerFactoryContext context;

  private final ApplicationProperties properties;

  public TenantServiceImpl(
      RepositoryFactory repositoryFactory,
      TransactionHandler transactionHandler,
      EncryptionService encryptionService,
      TenantEntityManagerFactoryContext tenantEntityManagerFactoryContext,
      ApplicationProperties properties) {
    super(repositoryFactory, transactionHandler);
    this.encryptionService = encryptionService;
    this.context = tenantEntityManagerFactoryContext;
    this.properties = properties;
  }

  private static final String VALID_DATABASE_NAME_REGEXP = "[A-Za-z0-9_]*";

  public EntityManagerFactory createEntityManagerFactory(Tenant tenant, File jarFile) {
    try {
      URL url = jarFile.toURI().toURL();
      URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {url});

      JdbcDriver dbmsType = properties.getJdbcDriver();
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

  @Override
  public void register(TenantInputModel model) throws Exception {
    transactionHandler.encapsulateTransaction(
        () -> {
          String tenantId = model.getName();
          String password = model.getPassword();
          String db = "tenant_" + tenantId;
          Tenant tenant = null;

          String encryptedPassword =
              encryptionService.encrypt(password, properties.getSecret(), properties.getSalt());

          TenantRepository tenantRepository = repositoryFactory.buildTenantRepository();
          Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
          if (tenantOptional.isPresent()) {
            tenant = tenantOptional.get();

            if (encryptedPassword != tenant.getPassword())
              throw new TenantRegistryException("The credentials doesn't match");
            tenant.setPackageName(model.getPackageName());
          } else {
            // Verify db string to prevent SQL injection
            if (!db.matches(VALID_DATABASE_NAME_REGEXP)) {
              throw new TenantRegistryException("Invalid db name: " + db);
            }
            String url = properties.getTenantUrlPrefix() + db;

            tenant = new Tenant(tenantId, db, encryptedPassword, url, model.getPackageName());
          }

          File jarFile = model.convertToJarFile();
          createDatabase(db, password, tenantId);

          EntityManagerFactory entityManagerFactory = createEntityManagerFactory(tenant, jarFile);

          context.register(tenant.getId(), entityManagerFactory);

          tenant = tenantRepository.save(tenant);
        });
  }

  public List<?> executeQuery(String tenantId, TenantQueryInputModel model) {
    EntityManager em = null;
    try {
      em = context.createEntityManager(tenantId);
      Query query = em.createQuery(model.getQuery());

      if (!CollectionUtils.isEmpty(model.getParameters())) {
        for (Map.Entry<String, Object> entry : model.getParameters().entrySet()) {
          query.setParameter(entry.getKey(), entry.getValue());
        }
      }

      return query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (em != null) em.close();
    }
  }

  private HikariDataSource createMasterDataSource() throws SQLException, ClassNotFoundException {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(properties.getMasterUrl());
    config.setUsername(properties.getMasterUser());
    config.setPassword(properties.getMasterPassword());
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(5);
    config.setConnectionTimeout(30000);
    config.setDriverClassName(properties.getJdbcDriver().getDriverClassName());
    return new HikariDataSource(config);
  }

  private void executeCommand(Connection connection, String command) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(command);
      getLog().info("Executing command '" + command + "'");
    }
  }

  private void createDatabase(String db, String password, String tenantId) {
    try (HikariDataSource ds = createMasterDataSource();
        Connection connection = ds.getConnection()) {
      // Create database
      String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + db;
      executeCommand(connection, createDatabaseSql);

      String dropUserSql = "DROP USER IF EXISTS " + tenantId;
      executeCommand(connection, dropUserSql);

      String createUserSql = "CREATE USER " + tenantId + " IDENTIFIED BY '" + password + "'";
      executeCommand(connection, createUserSql);

      String grantPrivilegesSql = "GRANT ALL PRIVILEGES ON " + db + ".* TO " + tenantId;
      executeCommand(connection, grantPrivilegesSql);

      getLog().info("Database and user created successfully.");
    } catch (Exception e) {
      throw new TenantRegistryException(
          "Error while creating credentials for tenant '" + tenantId + "'", e);
    }
  }
}
