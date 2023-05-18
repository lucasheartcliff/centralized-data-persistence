package com.cda.repository;

import com.cda.persistence.DatabaseContext;
import com.cda.repository.tenant.TenantRepository;
import com.cda.repository.tenant.TenantRepositoryImpl;

public class RepositoryFactoryImpl implements RepositoryFactory{
    private final DatabaseContext databaseContext;

    public RepositoryFactoryImpl(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

  @Override
    public TenantRepository buildTenantRepository(){
      return new TenantRepositoryImpl(databaseContext.getEntityManager());
  }
}
