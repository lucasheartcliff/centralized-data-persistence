package com.cda.configuration;

import java.util.Map;
import java.util.Properties;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties extends Properties {
  public String getSecret(){
    return getProperty("encryption.secret");
  }

  public String getSalt(){
    return getProperty("encryption.salt");
  }

  public String getTenantUrlPrefix(){
    return getProperty("multitenancy.tenant.url-prefix");
  }
}
