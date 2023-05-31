package com.cda.api;

import java.io.Serializable;

public interface TenantRegistry extends Serializable {
  String getName();

  String getPassword();

  String getPackageName();

  /** Jar file serialized with {@link java.util.Base64} */
  String getFile();

  String toJson();
}
