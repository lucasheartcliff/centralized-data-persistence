package com.cda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cda.RequestHandler;
import com.cda.configuration.ApplicationProperties;

@RestController
@RequestMapping("/api/tenant")
public class TenantController extends BaseController{
  private final static ApplicationProperties properties;
  @Autowired
  public TenantController(RequestHandler requestHandler, ApplicationProperties properties) {
    super(requestHandler);
    this. properties
  }

    @PostMapping("/")
    public ResponseEntity<Void> createTenant(@RequestParam String tenantId, @RequestParam String db, @RequestParam String password) {
   return encapsulateRequest((serviceFactory)->{
    tenantManagementService.createTenant(tenantId, db, password);
    
          return new ResponseEntity<>(HttpStatus.OK);

    });     

    }
 }
