package com.cda.exceptions;


public class TenantRegistryException extends RuntimeException{

  public TenantRegistryException(String message) {
    super(message);
  }

  public TenantRegistryException(String message, Throwable cause) {
    super(message, cause);
  }

}
