package com.jasongermaine.revoluttest.transfer;

import java.util.function.BiFunction;

import org.jdbi.v3.core.Handle;

import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;

public class TransferCreator implements BiFunction<TransferRequest, Handle, Transfer> {
  static final String INSERT_SQL = "INSERT INTO transfer " +
      "SET from_account_id=:from_account_id, " +
      "to_account_id=:to_account_id, " +
      "currency_code=:currency_code, " +
      "amount=:amount";

  @Override
  public Transfer apply(TransferRequest transferRequest, Handle handle) {
    int createdTransferId = handle.createUpdate(INSERT_SQL)
        .bind("from_account_id", transferRequest.getFromAccountId())
        .bind("to_account_id", transferRequest.getToAccountId())
        .bind("currency_code", transferRequest.getAmount().getCurrencyCode().toString())
        .bind("amount", transferRequest.getAmount().getAmount())
        .executeAndReturnGeneratedKeys()
        .mapTo(Integer.class)
        .one();

    return Transfer.builder()
        .from(transferRequest)
        .id(createdTransferId)
        .build();
  }
}
