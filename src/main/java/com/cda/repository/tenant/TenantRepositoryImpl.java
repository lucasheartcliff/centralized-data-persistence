package com.cda.repository.tenant;


import javax.persistence.EntityManager;

import com.cda.entities.Tenant;
import com.cda.repository.BaseRepository;

public class TenantRepositoryImpl extends BaseRepository<Tenant,String> implements TenantRepository{

  public TenantRepositoryImpl(EntityManager entityManager) {
    super(entityManager, Tenant.class);
  }

}
