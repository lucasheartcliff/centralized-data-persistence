package com.cda.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cda.api.TenantRegistryModel;

public class TenantControllerTest extends BaseControllerTest {
  private final TestRestTemplate restTemplate;

  @Autowired
  public TenantControllerTest(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Test
  public void shouldRegisterNewTenant() throws Exception {
    TenantRegistryModel tenantRegistryModel =
        new TenantRegistryModel(
            "TestTenant1", "", "com.cda.entities");

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/api/tenant",
            HttpMethod.POST,
            new HttpEntity<TenantRegistryModel>(tenantRegistryModel),
            String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
