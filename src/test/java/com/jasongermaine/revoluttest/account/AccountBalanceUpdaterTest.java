package com.jasongermaine.revoluttest.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Update;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.Money;
import com.neovisionaries.i18n.CurrencyCode;

@RunWith(MockitoJUnitRunner.class)
public class AccountBalanceUpdaterTest {
  private static final Account ACCOUNT = Account.builder()
      .id(1)
      .customerId(2)
      .balance(Money.of(CurrencyCode.USD, BigDecimal.TEN))
      .build();
  private static final Money NEW_BALANCE = Money.of(CurrencyCode.USD, BigDecimal.ZERO);

  private final AccountBalanceUpdater updater = new AccountBalanceUpdater();

  @Mock
  private Handle handle;
  @Mock
  private Update update;

  @Test
  public void itWillExecuteTheUpdate() {
    when(handle.createUpdate(AccountBalanceUpdater.UPDATE_SQL)).thenReturn(update);
    when(update.bind(anyString(), anyInt())).thenReturn(update);
    when(update.bind(anyString(), any(BigDecimal.class))).thenReturn(update);
    when(update.execute()).thenReturn(1);

    assertThat(updater.update(ACCOUNT, NEW_BALANCE, handle)).isEqualTo(1);

    verify(update).bind("id", ACCOUNT.getId());
    verify(update).bind("balance", NEW_BALANCE.getAmount());
  }
}
