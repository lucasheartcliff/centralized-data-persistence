package com.cda;

import java.io.File;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.cda.configuration.ApplicationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ServletInitializer extends SpringBootServletInitializer {
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    ApplicationProperties properties = getPropertiesOrFail();
    RequestHandler requestHandler = new RequestHandler(null);

    ConfigurableApplicationContext context = application.context();
    AutowireCapableBeanFactory autowireCapableBeanFactory = context.getAutowireCapableBeanFactory();
    autowireCapableBeanFactory.autowireBean(requestHandler);
    autowireCapableBeanFactory.autowireBean(properties);

    return application.sources(Main.class);
  }

  private ApplicationProperties getPropertiesOrFail() {
    try {
      ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
      ApplicationProperties properties = objectMapper.readValue(new File("application.yml"), ApplicationProperties.class);
      return properties;
    } catch (Exception e) {
      throw new RuntimeException(
          "The file : \"application.yml\" in resources folder must be setted up.");
    }
  }
}
