package com.cda.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TenantSelectQueryInputModel extends TenantQueryModel {
  private String query;
  private Map<String, Object> parameters;
  private String resultClassName;
}
