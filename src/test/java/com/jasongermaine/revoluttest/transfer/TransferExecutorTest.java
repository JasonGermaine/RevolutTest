package com.jasongermaine.revoluttest.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;
import com.jasongermaine.revoluttest.transfer.TransferExecutionCallback.TransferExecutionCallbackFactory;

@RunWith(MockitoJUnitRunner.class)
public class TransferExecutorTest {
  @Mock
  private Jdbi jdbi;
  @Mock
  private TransferExecutionCallbackFactory callbackFactory;
  @Mock
  private TransferExecutionCallback callback;
  @Mock
  private Transfer transfer;
  @Mock
  private TransferRequest transferRequest;
  private TransferExecutor transferExecutor;

  @Before
  public void beforeEach() {
    transferExecutor = new TransferExecutor(jdbi, callbackFactory);

    when(callbackFactory.create(transferRequest)).thenReturn(callback);
  }

  @Test
  public void itWillReturnTheTransferUponSuccess() throws Exception {
    when(jdbi.inTransaction(TransactionIsolationLevel.SERIALIZABLE, callback))
        .thenReturn(transfer);

    assertThat(transferExecutor.apply(transferRequest))
        .isEqualTo(transfer);
  }

  @Test
  public void itWillRethrowRuntimeExceptions() throws Exception {
    RuntimeException expectedException = new RuntimeException("ERROR");
    doThrow(expectedException)
        .when(jdbi)
        .inTransaction(TransactionIsolationLevel.SERIALIZABLE, callback);

    assertThatThrownBy(() -> transferExecutor.apply(transferRequest))
        .isEqualTo(expectedException);
  }

  @Test
  public void itWillPropagateCheckedExceptions() throws Exception {
    Exception expectedException = new Exception("ERROR");
    doThrow(expectedException)
        .when(jdbi)
        .inTransaction(TransactionIsolationLevel.SERIALIZABLE, callback);

    assertThatThrownBy(() -> transferExecutor.apply(transferRequest))
        .isInstanceOf(RuntimeException.class)
        .hasCause(expectedException);
  }
}
