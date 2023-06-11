package com.cda.api.commands;

import javax.annotation.Nonnull;

public class UpdateCommand extends QueryCommand {
  public UpdateCommand(@Nonnull Object content) {
    super(CommandType.UPDATE, gson.toJson(content), content.getClass().getName());
  }
}
