package com.cda.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cda.api.TenantRegistryModel;
import com.cda.api.commands.*;
import com.cda.testmodels.XEntity;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
  public void shouldExecuteInsertCommand() throws Exception {
    String token = registerTenantAndGetToken();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-tenant-token", token);

    XEntity xe = new XEntity();
    xe.setName(UUID.randomUUID().toString());

    InsertCommand insertCommand = new InsertCommand(xe);
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/api/tenant/command",
            HttpMethod.POST,
            new HttpEntity<>(Arrays.asList(insertCommand), headers),
            String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    XEntity xEntityResponse = deserializeResultResponse(response.getBody(), XEntity.class);

    assertEquals(xe.getName(), xEntityResponse.getName());
    assertNotNull(xEntityResponse.getCreatedAt());
    assertNotNull(xEntityResponse.getUpdatedAt());
    assertNotNull(xEntityResponse.getId());
  }

  private String registerTenantAndGetToken() {
    TenantRegistryModel tenantRegistryModel =
        new TenantRegistryModel("TestTenant2", "", "com.cda.testmodels");

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/api/tenant",
            HttpMethod.POST,
            new HttpEntity<TenantRegistryModel>(tenantRegistryModel),
            String.class);

    Map<String, Object> responseObject = deserializeResponse(response.getBody());
    String token = (String) responseObject.get("token");
    return token;
  }

  private XEntity deserializeResultResponse(String response, Class<XEntity> clazz) {
    if (response == null) return null;

    Type listType = new TypeToken<Map<String, List<XEntity>>>() {}.getType();
    return ((Map<String, List<XEntity>>) gson.fromJson(response, listType)).get("result").get(0);
  }

  private Map<String, String> deserializeResultResponse(String response) {
    if (response == null) return null;
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    return gson.fromJson(response, type);
  }

  private Map<String, Object> deserializeResponse(String response) {
    if (response == null) return null;
    return gson.fromJson(response, Map.class);
  }
}
