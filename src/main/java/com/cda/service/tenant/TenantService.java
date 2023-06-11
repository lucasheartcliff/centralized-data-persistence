package com.cda.service.tenant;

import java.util.List;

import com.cda.api.commands.QueryCommand;
import com.cda.model.TenantInputModel;

public interface TenantService {
  List<Object> executeQuery(String tenantId, List<QueryCommand> commands);
  String register(TenantInputModel model) throws Exception;
}
