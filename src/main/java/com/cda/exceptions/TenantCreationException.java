package com.cda.exceptions;


public class TenantCreationException extends RuntimeException{

  public TenantCreationException(String message) {
    super(message);
  }

  public TenantCreationException(String message, Throwable cause) {
    super(message, cause);
  }

}
