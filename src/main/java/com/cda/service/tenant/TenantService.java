package com.cda.service.tenant;

import java.util.List;

import com.cda.api.commands.QueryCommand;
import com.cda.model.TenantInputModel;

public interface TenantService {
  List<Object> executeCommands(String tenantToken, List<QueryCommand> commands);
  String register(TenantInputModel model) throws Exception;
}
