package com.jasongermaine.revoluttest.account;

import org.jdbi.v3.core.Handle;

import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.Money;

public class AccountBalanceUpdater {
  static final String UPDATE_SQL = "UPDATE account SET balance = :balance where id = :id";

  public int update(Account account, Money balance, Handle handle) {
    return handle.createUpdate(UPDATE_SQL)
        .bind("id", account.getId())
        .bind("balance", balance.getAmount())
        .execute();
  }
}
