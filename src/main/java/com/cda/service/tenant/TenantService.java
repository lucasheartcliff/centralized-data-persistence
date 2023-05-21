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
import com.cda.exceptions.TenantConnectionException;
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
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class TenantService extends BaseService {
  private final EncryptionService encryptionService;
  private final LiquibaseProperties liquibaseProperties;
  private final ResourceLoader resourceLoader;

  private final ApplicationProperties properties;

  public TenantService(
      RepositoryFactory repositoryFactory,
      TransactionHandler transactionHandler,
      EncryptionService encryptionService,
      LiquibaseProperties liquibaseProperties,
      ResourceLoader resourceLoader,
      ApplicationProperties properties) {
    super(repositoryFactory, transactionHandler);
    this.encryptionService = encryptionService;
    this.liquibaseProperties = liquibaseProperties;
    this.resourceLoader = resourceLoader;
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
      throw new TenantConnectionException(
          "Error while creating connection for tenant \"" + tenant.getId() + "\"", e);
    }
  }

  public void create(TenantInputModel model) throws Exception {
    transactionHandler.encapsulateTransaction(
        () -> {
          String tenantId = model.getName();
          String password = model.getPassword();
          String db = "tenant_" + tenantId;

          // Verify db string to prevent SQL injection
          if (!db.matches(VALID_DATABASE_NAME_REGEXP)) {
            throw new TenantCreationException("Invalid db name: " + db);
          }

          String url = properties.getTenantUrlPrefix() + db;
          String encryptedPassword =
              encryptionService.encrypt(password, properties.getSecret(), properties.getSalt());

          try {
            createDatabase(db, password);
          } catch (DataAccessException e) {
            throw new TenantCreationException("Error when creating db: " + db, e);
          }

          //  try (Connection connection = DriverManager.getConnection(url, db, password)) {
          //          DataSource tenantDataSource = new SingleConnectionDataSource(connection,
          // false);
          //          runLiquibase(tenantDataSource);
          //      } catch (SQLException | LiquibaseException e) {
          //          throw new TenantCreationException("Error when populating db: ", e);
          //      }
          //
          Tenant tenant = new Tenant(tenantId, db, encryptedPassword, url, model.getPackageName());

          TenantRepository tenantRepository = repositoryFactory.buildTenantRepository();
          return tenantRepository.save(tenant);
        });
  }

  private JdbcTemplate buildJdbcTemplate() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setUrl(properties.getMasterUrl());
    dataSource.setUsername(properties.getMasterUser());
    dataSource.setPassword(properties.getMasterPassword());

    return new JdbcTemplate(dataSource);
  }

  private void createDatabase(String db, String password) {
    JdbcTemplate jdbcTemplate = buildJdbcTemplate();
    jdbcTemplate.execute(
        (StatementCallback<Boolean>) stmt -> stmt.execute("CREATE DATABASE " + db));
    jdbcTemplate.execute(
        (StatementCallback<Boolean>)
            stmt ->
                stmt.execute("CREATE USER " + db + " WITH ENCRYPTED PASSWORD '" + password + "'"));
    jdbcTemplate.execute(
        (StatementCallback<Boolean>)
            stmt -> stmt.execute("GRANT ALL PRIVILEGES ON DATABASE " + db + " TO " + db));
  }

  private void runLiquibase(DataSource dataSource) throws LiquibaseException {
    SpringLiquibase liquibase = getSpringLiquibase(dataSource);
    liquibase.afterPropertiesSet();
  }

  protected SpringLiquibase getSpringLiquibase(DataSource dataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setResourceLoader(resourceLoader);
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog(liquibaseProperties.getChangeLog());
    liquibase.setContexts(liquibaseProperties.getContexts());
    return liquibase;
  }
}
