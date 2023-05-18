package com.cda;

import com.cda.configuration.ApplicationProperties;
import com.cda.persistence.PersistenceService;
import com.cda.persistence.PersistenceServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ServletInitializer extends SpringBootServletInitializer {
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    ApplicationProperties properties = getPropertiesOrFail();
    PersistenceService persistenceService = new PersistenceServiceImpl();
    EntityManagerFactory entityManagerFactory = persistenceService.buildEntityManagerFactory(properties);

    RequestHandler requestHandler = new RequestHandler(entityManagerFactory, properties);

    ConfigurableApplicationContext context = application.context();
    AutowireCapableBeanFactory autowireCapableBeanFactory = context.getAutowireCapableBeanFactory();
    autowireCapableBeanFactory.autowireBean(requestHandler);
    autowireCapableBeanFactory.autowireBean(properties);

    return application.sources(Main.class);
  }

  private ApplicationProperties getPropertiesOrFail() {
    try {
      ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
      ApplicationProperties properties =
          objectMapper.readValue(new File("application.yml"), ApplicationProperties.class);
      return properties;
    } catch (Exception e) {
      throw new RuntimeException(
          "The file : \"application.yml\" in resources folder must be setted up.");
    }
  }
}
