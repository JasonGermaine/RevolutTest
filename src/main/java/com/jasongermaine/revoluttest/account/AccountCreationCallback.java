package com.jasongermaine.revoluttest.account;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.AccountCreationRequest;

public class AccountCreationCallback implements HandleCallback<Account, Exception> {
  static final String INSERT_SQL = "INSERT INTO account " +
      "SET customer_id=:customer_id, " +
      "currency_code=:currency_code, " +
      "balance=:balance";

  private final AccountCreationRequest accountCreationRequest;

  @Inject
  public AccountCreationCallback(@Assisted AccountCreationRequest accountCreationRequest) {
    this.accountCreationRequest = accountCreationRequest;
  }

  @Override
  public Account withHandle(Handle handle) {
    int createdId = handle.createUpdate(INSERT_SQL)
        .bind("customer_id", accountCreationRequest.getCustomerId())
        .bind("currency_code", accountCreationRequest.getBalance().getCurrencyCode().toString())
        .bind("balance", accountCreationRequest.getBalance().getAmount())
        .executeAndReturnGeneratedKeys()
        .mapTo(Integer.class)
        .one();
    return Account.builder()
        .from(accountCreationRequest)
        .id(createdId)
        .build();
  }

  public interface AccountCreationCallbackFactory {
    AccountCreationCallback create(AccountCreationRequest request);
  }
}
