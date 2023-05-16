package com.cda.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfigProperties{
  private String urlPrefix;
  private String secret;
  private String salt;
}
