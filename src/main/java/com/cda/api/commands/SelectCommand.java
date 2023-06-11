package com.cda.api.commands;

import java.io.Serializable;
import java.util.Map;

public class SelectCommand extends QueryCommand {
  public SelectCommand(Query content) {
    super(CommandType.SELECT, gson.toJson(content), null);
  }

  public static class Query implements Serializable {
    private String query;
    private Map<String, Object> parameters;

    public Query(String query, Map<String, Object> parameters) {
      this.query = query;
      this.parameters = parameters;
    }

    public String getQuery() {
      return query;
    }

    public Map<String, Object> getParameters() {
      return parameters;
    }
  }
}
