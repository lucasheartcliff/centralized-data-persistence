package com.cda.service;

import com.cda.service.tenant.TenantService;

public interface ServiceFactory {
  TenantService buildTenantService();
}

