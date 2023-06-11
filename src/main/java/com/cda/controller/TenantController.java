package com.cda.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cda.RequestHandler;
import com.cda.api.commands.QueryCommand;
import com.cda.model.TenantInputModel;
import com.cda.service.tenant.TenantService;

@RestController
@RequestMapping("/api/tenant")
public class TenantController extends BaseController {
  @Autowired
  public TenantController(RequestHandler requestHandler) {
    super(requestHandler);
  }

  @PostMapping
  public ResponseEntity<?> createTenant(@RequestBody TenantInputModel model) {
    return encapsulateRequest(
        (serviceFactory) -> {
          TenantService tenantService = serviceFactory.buildTenantService();
          String token = tenantService.register(model);

          return buildOKResponse(buildResponse("token", token));
        });
  }

  @PostMapping("/command")
  public ResponseEntity<?> executeQuery(
      @RequestHeader("X-tenant-token") String tenantToken,
      @RequestBody List<QueryCommand> commands) {
    return encapsulateRequest(
        (serviceFactory) -> {
          TenantService tenantService = serviceFactory.buildTenantService();
          List<Object> result = tenantService.executeQuery(tenantToken, commands);
          return buildOKResponse(buildResponse("result", result));
        });
  }

  private Map<String, Object> buildResponse(String key, Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }
}
