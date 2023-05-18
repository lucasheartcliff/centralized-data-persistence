package com.cda.repository;

import com.cda.repository.tenant.TenantRepository;

public interface RepositoryFactory {
  TenantRepository buildTenantRepository();
}

