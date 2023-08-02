package com.cda.api.commands;

import com.cda.utils.ValueConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SelectCommand extends QueryCommand {
  public SelectCommand(Query content) {
    super(CommandType.SELECT, gson.toJson(content), null);
  }

  @Getter
  public static class Query implements Serializable {
    private String query;
    private Map<String, Argument> parameters;

    public Query(String query, Map<String, Object> parameters) {
      this.query = query;
      this.parameters = parseToArgument(parameters);
    }

    private Map<String, Argument> parseToArgument(Map<String, Object> parameters) {
      if (parameters == null) return new HashMap<>();
      Map<String, Argument> result = new HashMap<>();

      parameters.forEach(
          (key, value) -> {
            result.put(key, new Argument(value));
          });
      return result;
    }

    @AllArgsConstructor
    public static class Argument implements Serializable {
      private String value;
      private String valueType;

      public Argument(Object argument) {
        this(ValueConverter.serializeValue(argument), ValueConverter.getTypeName(argument));
      }

      public Object getValue() throws ParseException {
        return ValueConverter.deserializeValue(valueType, value);
      }
    }
  }
}
