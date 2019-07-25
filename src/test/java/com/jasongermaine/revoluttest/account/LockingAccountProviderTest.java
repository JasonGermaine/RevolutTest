package com.jasongermaine.revoluttest.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jasongermaine.revoluttest.core.Account;

@RunWith(MockitoJUnitRunner.class)
public class LockingAccountProviderTest {
  private final LockingAccountProvider lockingAccountProvider = new LockingAccountProvider();

  @Mock
  private Handle handle;
  @Mock
  private Query query;
  @Mock
  private ResultIterable<Account> result;
  @Mock
  private Account account;

  @Before
  public void beforeEach() {
    when(handle.createQuery(LockingAccountProvider.SELECT_SQL)).thenReturn(query);
    when(query.bind(anyString(), any(Integer.class))).thenReturn(query);
    when(query.mapTo(Account.class)).thenReturn(result);
  }

  @Test
  public void itWillReturnAccountWhenFound() {
    when(result.findOne()).thenReturn(Optional.of(account));

    assertThat(lockingAccountProvider.apply(123, handle)).isEqualTo(account);
    verify(query).bind("id", Integer.valueOf(123));
  }

  @Test
  public void itWillThrowWhenAccountNotFound() {
    when(result.findOne()).thenReturn(Optional.empty());

    assertThatThrownBy(() -> lockingAccountProvider.apply(123, handle))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No account found for id=123");
  }
}
