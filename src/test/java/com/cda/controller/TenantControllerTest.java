package com.cda.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cda.api.TenantRegistryModel;
import com.cda.model.TenantQueryInputModel;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TenantControllerTest extends BaseControllerTest {
  private final TestRestTemplate restTemplate;

  @Autowired
  public TenantControllerTest(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Test
  public void shouldRegisterNewTenant() throws Exception {
    TenantRegistryModel tenantRegistryModel =
        new TenantRegistryModel("TestTenant1", "", "com.cda.entities");

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/api/tenant",
            HttpMethod.POST,
            new HttpEntity<TenantRegistryModel>(tenantRegistryModel),
            String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseObject = deserializeResponse(response.getBody());

    assertNotNull(responseObject);
    assertNotNull(responseObject.getOrDefault("token", null));
  }

  @Test
  public void shouldExecuteQuery() throws Exception{
    //Registering tenant
    TenantRegistryModel tenantRegistryModel =
        new TenantRegistryModel("TestTenant2", "", "com.cda.entities");

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/api/tenant",
            HttpMethod.POST,
            new HttpEntity<TenantRegistryModel>(tenantRegistryModel),
            String.class);

    Map<String, Object> responseObject = deserializeResponse(response.getBody());
    String token = (String)responseObject.get("token");

     HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);

    new TenantQueryInputModel();

restTemplate.exchange("/api/tenant/query", HttpMethod.POST,new HttpEntity<T>(headers,))

  }

  private Map<String, Object> deserializeResponse(String response) {
    if (response == null) return null;
    return gson.fromJson(response, Map.class);
  }
}
