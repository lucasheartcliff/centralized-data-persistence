package com.cda.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ValueConverter {
  private static final Map<Class<?>, String> typeToName = new HashMap<>();

  static {
    typeToName.put(Integer.class, "Integer");
    typeToName.put(String.class, "String");
    typeToName.put(Double.class, "Double");
    typeToName.put(Long.class, "Long");
    typeToName.put(Date.class, "Date");
    typeToName.put(Boolean.class, "Boolean");
  }

  public static String getTypeName(Object value) {
    Class<?> valueType = value.getClass();
    if (!typeToName.containsKey(valueType)) {
      throw new IllegalArgumentException("Type not registered: " + valueType);
    }
    return typeToName.get(valueType);
  }

  public static String serializeValue(Object value) {
    if (value instanceof Date) {
      long timestamp = ((Date) value).getTime();
      return Long.toString(timestamp);
    }
    return value.toString();
  }

  public static Object deserializeValue(String typeName, String valueString) throws ParseException {
    for (Map.Entry<Class<?>, String> entry : typeToName.entrySet()) {
      if (entry.getValue().equals(typeName)) {
        Class<?> valueType = entry.getKey();
        if (valueType.equals(Integer.class)) {
          return Integer.parseInt(valueString);
        } else if (valueType.equals(String.class)) {
          return valueString;
        } else if (valueType.equals(Double.class)) {
          return Double.parseDouble(valueString);
        } else if (valueType.equals(Long.class)) {
          return Long.parseLong(valueString);
        } else if (valueType.equals(Date.class)) {
          long timestamp = Long.parseLong(valueString);
          return new Date(timestamp);
        } else if (valueType.equals(Boolean.class)) {
          return Boolean.parseBoolean(valueString);
        }
      }
    }
    throw new IllegalArgumentException("Type name not found: " + typeName);
  }
}
