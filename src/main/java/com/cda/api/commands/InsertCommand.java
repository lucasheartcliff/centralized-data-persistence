package com.cda.api.commands;

import javax.annotation.Nonnull;

public class InsertCommand extends QueryCommand {
  public InsertCommand(@Nonnull Object content) {
    super(CommandType.INSERT, gson.toJson(content), content.getClass().getName());
  }
}
