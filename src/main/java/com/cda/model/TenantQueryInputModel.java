package com.cda.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TenantQueryInputModel {
  private String query;
  private Map<String, Object> parameters;
  private String resultClassName;
}
