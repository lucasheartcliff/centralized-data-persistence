package com.cda.exceptions;

public class TenantContextException extends RuntimeException{

  public TenantContextException(String message) {
    super(message);
  }

  public TenantContextException(Throwable cause) {
    super(cause);
  }

  public TenantContextException(String message, Throwable cause) {
    super(message, cause);
  }

}
