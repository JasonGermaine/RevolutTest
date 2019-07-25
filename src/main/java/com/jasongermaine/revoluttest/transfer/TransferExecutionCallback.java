package com.jasongermaine.revoluttest.transfer;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jasongermaine.revoluttest.account.AccountBalanceUpdater;
import com.jasongermaine.revoluttest.account.LockingAccountProvider;
import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.Money;
import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;

public class TransferExecutionCallback implements HandleCallback<Transfer, Exception> {
  private static final int SLOW_CUSTOMER_ID = 12345;

  private final LockingAccountProvider lockingAccountProvider;
  private final AccountBalanceUpdater accountBalanceUpdater;
  private final TransferCreator transferCreator;
  private final TransferRequest transferRequest;

  @Inject
  public TransferExecutionCallback(LockingAccountProvider lockingAccountProvider,
                                   AccountBalanceUpdater accountBalanceUpdater,
                                   TransferCreator transferCreator,
                                   @Assisted TransferRequest transferRequest) {
    this.lockingAccountProvider = lockingAccountProvider;
    this.accountBalanceUpdater = accountBalanceUpdater;
    this.transferCreator = transferCreator;
    this.transferRequest = transferRequest;
  }

  @Override
  public Transfer withHandle(Handle handle) throws Exception {
    Account fromAccount = lockingAccountProvider.apply(transferRequest.getFromAccountId(), handle);
    Money newFromAccountBalance = fromAccount.getBalance().subtract(transferRequest.getAmount());

    Account toAccount = lockingAccountProvider.apply(transferRequest.getToAccountId(), handle);
    Money newToAccountBalance = toAccount.getBalance().add(transferRequest.getAmount());

    if (fromAccount.getCustomerId() == SLOW_CUSTOMER_ID) {
      Thread.sleep(1000);
    }
    accountBalanceUpdater.update(fromAccount, newFromAccountBalance, handle);
    accountBalanceUpdater.update(toAccount, newToAccountBalance, handle);
    return transferCreator.apply(transferRequest, handle);
  }

  public interface TransferExecutionCallbackFactory {
    TransferExecutionCallback create(TransferRequest request);
  }
}
