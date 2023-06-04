package com.cda.controller;

import com.cda.RequestHandler;
import com.cda.model.TenantInputModel;
import com.cda.model.TenantQueryInputModel;
import com.cda.service.tenant.TenantService;
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

  @PostMapping("/query")
  public ResponseEntity<?> executeQuery(
      @RequestHeader("tenant-token") String tenantToken,
      @RequestBody List<TenantQueryInputModel> models) {
    return encapsulateRequest(
        (serviceFactory) -> {
          TenantService tenantService = serviceFactory.buildTenantService();
          List<List<?>> result = tenantService.executeQuery(tenantToken, models);
          return buildOKResponse(buildResponse("result", result));
        });
  }

  private Map<String, Object> buildResponse(String key, Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }
}
