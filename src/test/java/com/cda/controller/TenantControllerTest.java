package com.cda.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cda.api.TenantRegistryModel;
import com.cda.api.commands.DeleteCommand;
import com.cda.api.commands.InsertCommand;
import com.cda.api.commands.QueryCommand;
import com.cda.api.commands.SelectCommand;
import com.cda.api.commands.UpdateCommand;
import com.cda.testmodels.XEntity;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
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
  public void shouldExecuteDeleteCommand() throws Exception {
    XEntity xEntityInsertResponse = executeInsertCommand();

    DeleteCommand updateCommand = new DeleteCommand(xEntityInsertResponse);
    ResponseEntity<String> response = executeRequest(Collections.singletonList(updateCommand));

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void shouldExecuteUpdateCommand() throws Exception {
    XEntity xEntityInsertResponse = executeInsertCommand();
    xEntityInsertResponse.setName("Edited x entity");

    UpdateCommand updateCommand = new UpdateCommand(xEntityInsertResponse);
    ResponseEntity<String> response = executeRequest(Collections.singletonList(updateCommand));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    XEntity xEntityUpdateResponse = deserializeResultResponse(response.getBody(), XEntity.class);

    assertEquals(xEntityInsertResponse.getName(), xEntityUpdateResponse.getName());
    assertNotNull(xEntityUpdateResponse.getCreatedAt());
    assertNotNull(xEntityUpdateResponse.getUpdatedAt());
    assertNotNull(xEntityUpdateResponse.getId());
  }

  @Test
  public void shouldExecuteSelectCommand() throws Exception {
    XEntity xEntityInsertResponse = executeInsertCommand();

    String query = "SELECT x FROM XEntity x WHERE x.id = :id";

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("id", xEntityInsertResponse.getId());

    SelectCommand selectCommand = new SelectCommand(new SelectCommand.Query(query, parameters));
    ResponseEntity<String> response = executeRequest(Collections.singletonList(selectCommand));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<XEntity> selectResponseList =
        deserializeSelectResultResponse(response.getBody(), XEntity.class);

    assertNotNull(selectResponseList);
    assertEquals(1, selectResponseList.size());

    XEntity xEntitySelectResponse = selectResponseList.get(0);
    assertEquals(xEntityInsertResponse.getName(), xEntitySelectResponse.getName());
    assertEquals(xEntityInsertResponse.getId(), xEntitySelectResponse.getId());
    assertEquals(xEntityInsertResponse.getCreatedAt(), xEntitySelectResponse.getCreatedAt());
    assertEquals(xEntityInsertResponse.getUpdatedAt(), xEntitySelectResponse.getUpdatedAt());
  }

  @Test
  public void shouldExecuteInsertCommand() throws Exception {
    XEntity xe = new XEntity();
    xe.setName(UUID.randomUUID().toString());

    InsertCommand insertCommand = new InsertCommand(xe);
    ResponseEntity<String> response = executeRequest(Collections.singletonList(insertCommand));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    XEntity xEntityResponse = deserializeResultResponse(response.getBody(), XEntity.class);

    assertEquals(xe.getName(), xEntityResponse.getName());
    assertNotNull(xEntityResponse.getCreatedAt());
    assertNotNull(xEntityResponse.getUpdatedAt());
    assertNotNull(xEntityResponse.getId());
  }

  private XEntity executeInsertCommand() {
    XEntity xEntity = new XEntity();
    xEntity.setName(UUID.randomUUID().toString());

    InsertCommand insertCommand = new InsertCommand(xEntity);
    ResponseEntity<String> responseInsert =
        executeRequest(Collections.singletonList(insertCommand));

    assertEquals(HttpStatus.OK, responseInsert.getStatusCode());
    return deserializeResultResponse(responseInsert.getBody(), XEntity.class);
  }

  private ResponseEntity<String> executeRequest(List<QueryCommand> commands) {
    String token = registerTenantAndGetToken();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-tenant-token", token);

    return restTemplate.exchange(
        "/api/tenant/command", HttpMethod.POST, new HttpEntity<>(commands, headers), String.class);
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

  private List<XEntity> deserializeSelectResultResponse(String response, Class<XEntity> clazz) {
    if (response == null) return null;

    Type listType = new TypeToken<Map<String, List<List<XEntity>>>>() {}.getType();
    return ((Map<String, List<List<XEntity>>>) gson.fromJson(response, listType))
        .get("result")
        .get(0);
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
