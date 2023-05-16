package com.cda;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ServletInitializer extends SpringBootServletInitializer {
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

    RequestHandler requestHandler = new RequestHandler(null);

    ConfigurableApplicationContext context = application.context();
    AutowireCapableBeanFactory autowireCapableBeanFactory = context.getAutowireCapableBeanFactory();
    autowireCapableBeanFactory.autowireBean(requestHandler);

    return application.sources(Main.class);
  }
}
