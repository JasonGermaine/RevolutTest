package com.jasongermaine.revoluttest.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.jasongermaine.revoluttest.core.Money;
import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;
import com.neovisionaries.i18n.CurrencyCode;

@RunWith(MockitoJUnitRunner.class)
public class TransferCreatorTest {
  private static final TransferRequest REQUEST = TransferRequest.builder()
      .fromAccountId(1)
      .toAccountId(2)
      .amount(Money.of(CurrencyCode.USD, BigDecimal.TEN))
      .build();

  private final TransferCreator creator = new TransferCreator();

  @Mock
  private Handle handle;
  @Mock
  private Update update;
  @Mock
  private ResultBearing resultBearing;
  @Mock
  private ResultIterable<Integer> creationResult;

  @Test
  public void itWillCreateTransferWithHandle() {
    int createdId = 333;

    when(handle.createUpdate(TransferCreator.INSERT_SQL)).thenReturn(update);
    when(update.bind(anyString(), anyInt())).thenReturn(update);
    when(update.bind(anyString(), anyString())).thenReturn(update);
    when(update.bind(anyString(), any(BigDecimal.class))).thenReturn(update);
    when(update.executeAndReturnGeneratedKeys()).thenReturn(resultBearing);
    when(resultBearing.mapTo(Integer.class)).thenReturn(creationResult);
    when(creationResult.one()).thenReturn(createdId);

    assertThat(creator.apply(REQUEST, handle))
        .isEqualTo(Transfer.builder()
            .from(REQUEST)
            .id(createdId)
            .build());
    verify(update).bind("from_account_id", REQUEST.getFromAccountId());
    verify(update).bind("to_account_id", REQUEST.getToAccountId());
    verify(update).bind("currency_code", REQUEST.getAmount().getCurrencyCode().toString());
    verify(update).bind("amount", REQUEST.getAmount().getAmount());
  }
}
