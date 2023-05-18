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

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cda.configuration.ApplicationProperties;
import com.cda.utils.JdbcDriver;
import com.google.common.collect.ImmutableMap;

@Configuration
public class PersistenceServiceImpl implements PersistenceService {
  @Override
  @Bean
  public EntityManagerFactory buildEntityManagerFactory(ApplicationProperties properties) {
    JdbcDriver dbmsType = JdbcDriver.valueOf(properties.getDbmsName());
    return new HibernatePersistenceProvider()
        .createContainerEntityManagerFactory(
            buildPersistenceUnitInfo(),
            ImmutableMap.<String, Object>builder()
                .put(JPA_JDBC_DRIVER, dbmsType.getDriverClassName())
                .put(JPA_JDBC_URL, properties.getMasterUrl())
                .put(USER, properties.getMasterUser())
                .put(PASS, properties.getMasterPassword())
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
                .build());
  }

  private PersistenceUnitInfo buildPersistenceUnitInfo() {
    return new PersistenceUnitInfoImpl();
  }
}
