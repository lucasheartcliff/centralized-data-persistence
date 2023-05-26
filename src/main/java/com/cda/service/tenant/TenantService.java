package com.cda.service.tenant;

import com.cda.model.TenantInputModel;

public interface TenantService {
  void register(TenantInputModel model) throws Exception;
}
