package com.cda.exceptions;


public class TenantCommandExecutionException extends RuntimeException{

  public TenantCommandExecutionException(String message) {
    super(message);
  }

  public TenantCommandExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
