package com.jasongermaine.revoluttest.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.statement.StatementContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.Money;
import com.neovisionaries.i18n.CurrencyCode;

@RunWith(MockitoJUnitRunner.class)
public class AccountMapperTest {
  @Mock
  private ResultSet resultSet;
  @Mock
  private StatementContext statementContext;
  private final AccountMapper mapper = new AccountMapper();

  @Test
  public void itWillMapResultSetToAnAccount() throws SQLException {
    int id = 1;
    int customerId = 2;
    String currencyCode = CurrencyCode.EUR.toString();
    BigDecimal balance = BigDecimal.TEN;

    when(resultSet.getInt("id")).thenReturn(id);
    when(resultSet.getInt("customer_id")).thenReturn(customerId);
    when(resultSet.getString("currency_code")).thenReturn(currencyCode);
    when(resultSet.getBigDecimal("balance")).thenReturn(balance);

    assertThat(mapper.map(resultSet, statementContext))
        .isEqualTo(Account.builder()
            .id(id)
            .customerId(customerId)
            .balance(Money.of(CurrencyCode.EUR, balance))
            .build());
  }
}
