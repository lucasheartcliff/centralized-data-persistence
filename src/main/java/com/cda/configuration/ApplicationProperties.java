package com.cda.configuration;

import com.cda.utils.JdbcDriver;
import java.util.Properties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class ApplicationProperties extends Properties {
  public String getSecret() {
    return getProperty("encryption.secret");
  }

  public String getSalt() {
    return getProperty("encryption.salt");
  }

  public String getDbmsName() {
    return getProperty("dbms.name");
  }

  public String getMasterUser() {
    return getProperty("multitenancy.master.username");
  }

  public String getMasterPassword() {
    return getProperty("multitenancy.master.password");
  }

  public String getMasterUrl() {
    return getProperty("multitenancy.master.url");
  }

  public String getTenantPort() {
    return getProperty("multitenancy.tenant.port");
  }

  public String getTenantHost() {
    return getProperty("multitenancy.tenant.host");
  }

  public String getTenantUrlPrefix() {
    return "jdbc:" + getDbmsName() + "://" + getTenantHost() + ":" + getTenantPort() + "/";
  }

  public JdbcDriver getJdbcDriver() {
    return JdbcDriver.getByName(getDbmsName());
  }
}
