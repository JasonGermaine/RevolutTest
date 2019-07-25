package com.jasongermaine.revoluttest.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jasongermaine.revoluttest.account.AccountCreationCallback.AccountCreationCallbackFactory;
import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.AccountCreationRequest;
import com.jasongermaine.revoluttest.core.Money;
import com.neovisionaries.i18n.CurrencyCode;

@RunWith(MockitoJUnitRunner.class)
public class AccountCreatorTest {
  private static final AccountCreationRequest REQUEST = AccountCreationRequest.builder()
      .customerId(1)
      .balance(Money.of(CurrencyCode.USD))
      .build();

  @Mock
  private Jdbi jdbi;
  @Mock
  private AccountCreationCallbackFactory callbackFactory;
  @Mock
  private AccountCreationCallback callback;
  @Mock
  private Account account;
  private AccountCreator creator;

  @Before
  public void beforeEach() {
    creator = new AccountCreator(jdbi, callbackFactory);

    when(callbackFactory.create(REQUEST)).thenReturn(callback);
  }

  @Test
  public void itWillReturnTheAccountUponSuccess() throws Exception {
    when(jdbi.withHandle(callback)).thenReturn(account);

    assertThat(creator.apply(REQUEST)).isEqualTo(account);
  }

  @Test
  public void itWillRethrowRuntimeExceptions() throws Exception {
    RuntimeException expectedException = new RuntimeException("ERROR");
    doThrow(expectedException)
        .when(jdbi)
        .withHandle(callback);

    assertThatThrownBy(() -> creator.apply(REQUEST))
        .isEqualTo(expectedException);
  }

  @Test
  public void itWillPropagateCheckedExceptions() throws Exception {
    Exception expectedException = new Exception("ERROR");
    doThrow(expectedException)
        .when(jdbi)
        .withHandle(callback);

    assertThatThrownBy(() -> creator.apply(REQUEST))
        .isInstanceOf(RuntimeException.class)
        .hasCause(expectedException);
  }

  @Test
  public void itWillPropagateConstraintViolationsAsIllegalArgumentException() throws Exception {
    RuntimeException initialException = new UnableToExecuteStatementException("UNIQUE_CUSTOMER_CURRENCY_ACCOUNT");
    doThrow(initialException)
        .when(jdbi)
        .withHandle(callback);

    assertThatThrownBy(() -> creator.apply(REQUEST))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Customer=%d already maintains an account in currency=%s", REQUEST.getCustomerId(), REQUEST.getBalance().getCurrencyCode())
        .hasCause(initialException);
  }
}
