package com.jasongermaine.revoluttest.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.jdbi.v3.core.Handle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jasongermaine.revoluttest.account.AccountBalanceUpdater;
import com.jasongermaine.revoluttest.account.LockingAccountProvider;
import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.Money;
import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;
import com.neovisionaries.i18n.CurrencyCode;

@RunWith(MockitoJUnitRunner.class)
public class TransferExecutionCallbackTest {
  private static final TransferRequest TRANSFER_REQUEST = TransferRequest.builder()
      .fromAccountId(1)
      .toAccountId(2)
      .amount(Money.of(CurrencyCode.USD, BigDecimal.TEN))
      .build();
  private static final Account FROM_ACCOUNT = Account.builder()
      .id(1)
      .customerId(2)
      .balance(Money.of(CurrencyCode.USD, BigDecimal.TEN))
      .build();
  private static final Account TO_ACCOUNT = Account.builder()
      .id(2)
      .customerId(1)
      .balance(Money.of(CurrencyCode.USD, BigDecimal.ZERO))
      .build();

  @Mock
  private LockingAccountProvider lockingAccountProvider;
  @Mock
  private AccountBalanceUpdater accountBalanceUpdater;
  @Mock
  private TransferCreator transferCreator;
  @Mock
  private Handle handle;
  @Mock
  private Transfer transfer;
  private TransferExecutionCallback transferExecutionCallback;

  @Before
  public void beforeEach() {
    transferExecutionCallback = new TransferExecutionCallback(
        lockingAccountProvider,
        accountBalanceUpdater,
        transferCreator,
        TRANSFER_REQUEST);
  }

  @Test
  public void itWillExecuteTheTransfer() throws Exception {
    when(lockingAccountProvider.apply(TRANSFER_REQUEST.getFromAccountId(), handle))
        .thenReturn(FROM_ACCOUNT);
    when(lockingAccountProvider.apply(TRANSFER_REQUEST.getToAccountId(), handle))
        .thenReturn(TO_ACCOUNT);
    when(accountBalanceUpdater.update(any(Account.class), any(Money.class), eq(handle)))
        .thenReturn(1);
    when(transferCreator.apply(TRANSFER_REQUEST, handle))
        .thenReturn(transfer);

    assertThat(transferExecutionCallback.withHandle(handle))
        .isEqualTo(transfer);
    verify(accountBalanceUpdater)
        .update(FROM_ACCOUNT, Money.of(CurrencyCode.USD, BigDecimal.ZERO), handle);
    verify(accountBalanceUpdater)
        .update(TO_ACCOUNT, Money.of(CurrencyCode.USD, BigDecimal.TEN), handle);
  }
}
