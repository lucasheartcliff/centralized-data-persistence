package com.cda.exceptions;

public class TenantConnectionException extends RuntimeException{

  public TenantConnectionException(String message) {
    super(message);
  }

  public TenantConnectionException(Throwable cause) {
    super(cause);
  }

  public TenantConnectionException(String message, Throwable cause) {
    super(message, cause);
  }

}
