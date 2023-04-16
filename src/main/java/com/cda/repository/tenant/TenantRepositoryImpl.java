package com.cda.repository.tenant;


import javax.persistence.EntityManager;

import com.cda.model.Tenant;
import com.cda.repository.BaseRepository;

public class TenantRepositoryImpl extends BaseRepository<Tenant,String>{

  public TenantRepositoryImpl(EntityManager entityManager) {
    super(entityManager, Tenant.class);
  }

}
