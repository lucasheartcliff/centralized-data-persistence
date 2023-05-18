package com.cda.service.tenant;

import com.cda.entities.Tenant;
import com.cda.exceptions.TenantCreationException;
import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;
import com.cda.repository.tenant.TenantRepository;
import com.cda.service.BaseService;
import com.cda.utils.EncryptionService;

import javax.sql.DataSource;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

public class TenantService extends BaseService {
  private final EncryptionService encryptionService;
  private final JdbcTemplate jdbcTemplate;
  private final LiquibaseProperties liquibaseProperties;
  private final ResourceLoader resourceLoader;

  private final String urlPrefix;
  private final String secret;
  private final String salt;

  public TenantService(
      RepositoryFactory repositoryFactory,
      TransactionHandler transactionHandler,
      EncryptionService encryptionService,
      JdbcTemplate jdbcTemplate,
      LiquibaseProperties liquibaseProperties,
      ResourceLoader resourceLoader,
      String urlPrefix,
      String secret,
      String salt) {
    super(repositoryFactory, transactionHandler);
    this.encryptionService = encryptionService;
    this.jdbcTemplate = jdbcTemplate;
    this.liquibaseProperties = liquibaseProperties;
    this.resourceLoader = resourceLoader;
    this.urlPrefix = urlPrefix;
    this.secret = secret;
    this.salt = salt;
  }

  private static final String VALID_DATABASE_NAME_REGEXP = "[A-Za-z0-9_]*";

  public void create(String tenantId, String db, String password) {

    // Verify db string to prevent SQL injection
    if (!db.matches(VALID_DATABASE_NAME_REGEXP)) {
      throw new TenantCreationException("Invalid db name: " + db);
    }

    String url = urlPrefix + db;
    String encryptedPassword = encryptionService.encrypt(password, secret, salt);
    try {
      createDatabase(db, password);
    } catch (DataAccessException e) {
      throw new TenantCreationException("Error when creating db: " + db, e);
    }

    //  try (Connection connection = DriverManager.getConnection(url, db, password)) {
    //          DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
    //          runLiquibase(tenantDataSource);
    //      } catch (SQLException | LiquibaseException e) {
    //          throw new TenantCreationException("Error when populating db: ", e);
    //      }
    //
    Tenant tenant =
        Tenant.builder().id(tenantId).db(db).url(url).password(encryptedPassword).build();

    TenantRepository tenantRepository = repositoryFactory.buildTenantRepository();
    tenantRepository.save(tenant);
  }

  private void createDatabase(String db, String password) {
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