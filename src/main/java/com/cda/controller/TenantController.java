package com.cda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cda.RequestHandler;
import com.cda.model.TenantInputModel;
import com.cda.model.TenantQueryInputModel;
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
          tenantService.register(model);

          return buildOKResponse();
        });
  }

  @PostMapping("/query")
  public ResponseEntity<?> executeQuery(@RequestBody TenantQueryInputModel model) {
    return encapsulateRequest(
        (serviceFactory) -> {
        TenantService tenantService = serviceFactory.buildTenantService();

          return buildOKResponse();
        });
  }

  
}
