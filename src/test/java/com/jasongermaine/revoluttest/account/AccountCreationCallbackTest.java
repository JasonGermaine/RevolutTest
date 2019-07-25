package com.jasongermaine.revoluttest.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultBearing;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Update;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.AccountCreationRequest;
import com.jasongermaine.revoluttest.core.Money;
import com.neovisionaries.i18n.CurrencyCode;

@RunWith(MockitoJUnitRunner.class)
public class AccountCreationCallbackTest {
  private static final AccountCreationRequest REQUEST = AccountCreationRequest.builder()
      .customerId(1)
      .balance(Money.of(CurrencyCode.USD))
      .build();

  @Mock
  private Handle handle;
  @Mock
  private Update update;
  @Mock
  private ResultBearing resultBearing;
  @Mock
  private ResultIterable<Integer> creationResult;
  private final AccountCreationCallback accountCreationCallback = new AccountCreationCallback(REQUEST);

  @Test
  public void itWillCreateAccountWithHandle() {
    int createdId = 333;

    when(handle.createUpdate(AccountCreationCallback.INSERT_SQL)).thenReturn(update);
    when(update.bind(anyString(), anyInt())).thenReturn(update);
    when(update.bind(anyString(), anyString())).thenReturn(update);
    when(update.bind(anyString(), any(BigDecimal.class))).thenReturn(update);
    when(update.executeAndReturnGeneratedKeys()).thenReturn(resultBearing);
    when(resultBearing.mapTo(Integer.class)).thenReturn(creationResult);
    when(creationResult.one()).thenReturn(createdId);

    assertThat(accountCreationCallback.withHandle(handle))
        .isEqualTo(Account.builder()
            .from(REQUEST)
            .id(createdId)
            .build());
    verify(update).bind("customer_id", REQUEST.getCustomerId());
    verify(update).bind("currency_code", REQUEST.getBalance().getCurrencyCode().toString());
    verify(update).bind("balance", REQUEST.getBalance().getAmount());
  }
}
