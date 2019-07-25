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

import com.jasongermaine.revoluttest.core.Money;
import com.jasongermaine.revoluttest.core.Transfer;
import com.neovisionaries.i18n.CurrencyCode;

@RunWith(MockitoJUnitRunner.class)
public class TransferMapperTest {

  @Mock
  private ResultSet resultSet;
  @Mock
  private StatementContext statementContext;
  private final TransferMapper mapper = new TransferMapper();

  @Test
  public void itWillMapResultSetToATransfer() throws SQLException {
    int id = 1;
    int fromAccountId = 2;
    int toAccountId = 3;
    String currencyCode = CurrencyCode.EUR.toString();
    BigDecimal amount = BigDecimal.TEN;

    when(resultSet.getInt("id")).thenReturn(id);
    when(resultSet.getInt("from_account_id")).thenReturn(fromAccountId);
    when(resultSet.getInt("to_account_id")).thenReturn(toAccountId);
    when(resultSet.getString("currency_code")).thenReturn(currencyCode);
    when(resultSet.getBigDecimal("amount")).thenReturn(amount);

    assertThat(mapper.map(resultSet, statementContext))
        .isEqualTo(Transfer.builder()
            .id(id)
            .fromAccountId(fromAccountId)
            .toAccountId(toAccountId)
            .amount(Money.of(CurrencyCode.EUR, amount))
            .build());
  }
}
