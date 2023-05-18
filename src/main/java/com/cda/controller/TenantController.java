package com.cda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cda.RequestHandler;
import com.cda.service.tenant.TenantService;

@RestController
@RequestMapping("/api/tenant")
public class TenantController extends BaseController {
  @Autowired
  public TenantController(RequestHandler requestHandler) {
    super(requestHandler);
  }

  @PostMapping
  public ResponseEntity<?> createTenant(
      @RequestParam String tenantId, @RequestParam String db, @RequestParam String password) {
    return encapsulateRequest(
        (serviceFactory) -> {
        TenantService tenantService = serviceFactory.buildTenantService();
          tenantService.create(tenantId, db, password);

          return buildOKResponse();
        });
  }
}
