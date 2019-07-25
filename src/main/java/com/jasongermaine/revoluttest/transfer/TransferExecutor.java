package com.jasongermaine.revoluttest.transfer;

import java.util.function.Function;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;

import com.google.inject.Inject;
import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;
import com.jasongermaine.revoluttest.transfer.TransferExecutionCallback.TransferExecutionCallbackFactory;

public class TransferExecutor implements Function<TransferRequest, Transfer> {
  private final Jdbi jdbi;
  private final TransferExecutionCallbackFactory callbackFactory;

  @Inject
  public TransferExecutor(Jdbi jdbi, TransferExecutionCallbackFactory callbackFactory) {
    this.jdbi = jdbi;
    this.callbackFactory = callbackFactory;
  }

  @Override
  public Transfer apply(TransferRequest transferRequest) {
    try {
      return jdbi.inTransaction(TransactionIsolationLevel.SERIALIZABLE, callbackFactory.create(transferRequest));
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw ((RuntimeException) e);
      }

      throw new RuntimeException(e);
    }
  }
}
