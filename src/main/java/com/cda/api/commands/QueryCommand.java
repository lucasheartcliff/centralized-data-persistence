package com.cda.api.commands;

import java.io.Serializable;

import com.google.gson.Gson;

public class QueryCommand implements Serializable {
  private CommandType commandType;
  private String content;
  private String className;
  protected static transient final Gson gson = new Gson(); 

  protected QueryCommand(CommandType commandType, String content, String className) {
    this.commandType = commandType;
    this.content = content;
    this.className = className;
  }

  public CommandType getCommandType() {
    return commandType;
  }

  public String getContent() {
    return content;
  }

  public <T extends Object> T getParsedContent(Class<T> clazz){
    return gson.fromJson(getContent(), clazz);
  }

  public String getClassName() {
    return className;
  }

  @Override
  public String toString() {
    return gson.toJson(this);
  }


  
}
