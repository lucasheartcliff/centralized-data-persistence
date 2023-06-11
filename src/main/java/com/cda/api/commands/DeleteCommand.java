package com.cda.api.commands;

import javax.annotation.Nonnull;

public class DeleteCommand extends QueryCommand {
  public DeleteCommand(@Nonnull Object content) {
    super(CommandType.DELETE, gson.toJson(content), content.getClass().getName());
  }
}
