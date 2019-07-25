package com.jasongermaine.revoluttest.account;

import java.util.function.Function;

import org.jdbi.v3.core.Jdbi;

import com.google.inject.Inject;
import com.jasongermaine.revoluttest.account.AccountCreationCallback.AccountCreationCallbackFactory;
import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.AccountCreationRequest;

public class AccountCreator implements Function<AccountCreationRequest, Account> {
  private final Jdbi jdbi;
  private final AccountCreationCallbackFactory callbackFactory;

  @Inject
  public AccountCreator(Jdbi jdbi, AccountCreationCallbackFactory callbackFactory) {
    this.jdbi = jdbi;
    this.callbackFactory = callbackFactory;
  }

  @Override
  public Account apply(AccountCreationRequest request) {
    try {
      return jdbi.withHandle(callbackFactory.create(request));
    } catch (Exception e) {
      // example error handling of constraint violations
      if (e.getMessage().contains("UNIQUE_CUSTOMER_CURRENCY_ACCOUNT")) {
        throw new IllegalArgumentException(String.format("Customer=%d already maintains an account in currency=%s", request.getCustomerId(), request.getBalance().getCurrencyCode()), e);
      }
      if (e instanceof RuntimeException) {
        throw ((RuntimeException) e);
      }
      throw new RuntimeException(e);
    }
  }
}
