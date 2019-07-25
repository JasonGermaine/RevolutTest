package com.jasongermaine.revoluttest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.Test;

import com.neovisionaries.i18n.CurrencyCode;

public class TransferRequestTest {

  @Test
  public void itWillBuildATransferToDifferentCustomers() {
    int fromAccountId = 1;
    int toAccountId = 2;
    Money money = Money.of(CurrencyCode.USD, BigDecimal.TEN);

    assertThat(TransferRequest.builder()
        .fromAccountId(fromAccountId)
        .toAccountId(toAccountId)
        .amount(money)
        .build())
        .isEqualTo(TransferRequest.builder()
            .fromAccountId(fromAccountId)
            .toAccountId(toAccountId)
            .amount(money)
            .build());
  }

  @Test
  public void itWillFailToBuildForTheSameCustomer() {
    assertThatThrownBy(() -> TransferRequest.builder()
        .fromAccountId(1)
        .toAccountId(1)
        .amount(Money.of(CurrencyCode.USD, BigDecimal.TEN))
        .build())
        .isInstanceOf(IllegalArgumentException.class);
  }
}
