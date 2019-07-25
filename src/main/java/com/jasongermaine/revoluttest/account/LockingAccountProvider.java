package com.jasongermaine.revoluttest.account;

import java.util.function.BiFunction;

import org.jdbi.v3.core.Handle;

import com.jasongermaine.revoluttest.core.Account;

public class LockingAccountProvider implements BiFunction<Integer, Handle, Account> {
  static final String SELECT_SQL = "SELECT * FROM account WHERE id = :id FOR UPDATE";

  @Override
  public Account apply(Integer accountId, Handle handle) {
    return handle.createQuery(SELECT_SQL)
        .bind("id", accountId)
        .mapTo(Account.class)
        .findOne()
        .orElseThrow(() -> new IllegalArgumentException("No account found for id=" + accountId));
  }
}
