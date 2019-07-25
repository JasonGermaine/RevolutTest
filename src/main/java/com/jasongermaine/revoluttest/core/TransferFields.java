package com.jasongermaine.revoluttest.core;

import org.immutables.value.Value;

import com.google.common.base.Preconditions;

public interface TransferFields {
  int getFromAccountId();

  int getToAccountId();

  Money getAmount();

  @Value.Check
  default void validate() {
    Preconditions.checkArgument(
        getFromAccountId() != getToAccountId(),
        "Self transfers are not supported");
  }
}
