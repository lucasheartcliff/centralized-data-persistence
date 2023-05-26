package com.cda.utils;

public enum JdbcDriver {
  MYSQL("com.mysql.jdbc.Driver"),
  MARIADB("org.mariadb.jdbc.Driver"),
  POSTGRES("org.postgresql.Driver"),
  ORACLE("oracle.jdbc.driver.OracleDriver"),
  SQL_SERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver");

  private String driverClassName;

  JdbcDriver(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public static JdbcDriver getByName(String name){
    if(name == null) return null;

    return valueOf(name.toUpperCase());
  }
}
