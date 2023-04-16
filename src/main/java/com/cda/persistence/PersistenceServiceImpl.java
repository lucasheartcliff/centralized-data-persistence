package com.cda.persistence;

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

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableMap;

@Configuration
public class PersistenceServiceImpl implements PersistenceService {
  private final String JDBC_URL = "jdbc:mysql://localhost:3306/mbdb?autoReconnect=true&characterEncoding=utf-8&useTimezone=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
  private final String USERNAME = "mb";
  private final String PASSWORD = "";

  @Override
  @Bean
  public EntityManagerFactory buildEntityManagerFactory() {
    return new HibernatePersistenceProvider()
        .createContainerEntityManagerFactory(
            buildPersistenceUnitInfo(),
            ImmutableMap.<String, Object>builder()
                .put(JPA_JDBC_DRIVER, "org.mariadb.jdbc.Driver")
                .put(JPA_JDBC_URL, JDBC_URL)
                .put(USER, USERNAME)
                .put(PASS, PASSWORD)
                .put(DIALECT, MySQL5Dialect.class)
                .put(HBM2DDL_AUTO, "update")
                .put(SHOW_SQL, true)
                .put(QUERY_STARTUP_CHECKING, false)
                .put(GENERATE_STATISTICS, false)
                .put(USE_REFLECTION_OPTIMIZER, false)
                .put(USE_SECOND_LEVEL_CACHE, false)
                .put(USE_QUERY_CACHE, false)
                .put(USE_STRUCTURED_CACHE, false)
                .put(STATEMENT_BATCH_SIZE, 20)
                .put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE)
                .put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver)
                .put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider)
                .build());

  }

  private PersistenceUnitInfo buildPersistenceUnitInfo() {
    return new PersistenceUnitInfoImpl();
  }
}
