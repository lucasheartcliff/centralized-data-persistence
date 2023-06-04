package com.cda.service.tenant;

import java.util.List;

import com.cda.model.TenantInputModel;
import com.cda.model.TenantQueryInputModel;

public interface TenantService {
  List<List<?>> executeQuery(String tenantId, List<TenantQueryInputModel> models);
  String register(TenantInputModel model) throws Exception;
}
