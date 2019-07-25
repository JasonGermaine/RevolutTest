package com.jasongermaine.revoluttest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.Test;

import com.neovisionaries.i18n.CurrencyCode;

public class MoneyTest {

  @Test
  public void itWillBuildAndSetScaleForAValidAmount() {
    CurrencyCode currencyCode = CurrencyCode.USD;
    BigDecimal amount = BigDecimal.TEN;

    Money money = Money.of(currencyCode, amount);
    assertThat(money.getAmount().scale()).isEqualTo(6);
  }

  @Test
  public void itWillFailToBuildForNegativeAmount() {
    CurrencyCode currencyCode = CurrencyCode.USD;
    BigDecimal amount = BigDecimal.TEN.negate();

    assertThatThrownBy(() -> Money.of(currencyCode, amount))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Money must not be of a negative amount");
  }

  @Test
  public void itWillAddTwoMonies() {
    assertThat(Money.of(CurrencyCode.USD, BigDecimal.TEN)
        .add(Money.of(CurrencyCode.USD, BigDecimal.valueOf(6L))))
        .isEqualTo(Money.of(CurrencyCode.USD, BigDecimal.valueOf(16L)));
  }

  @Test
  public void itWillThrowWhenAddingMoniesOfDifferentCurrency() {
    assertThatThrownBy(() -> Money.of(CurrencyCode.USD, BigDecimal.TEN)
        .add(Money.of(CurrencyCode.EUR, BigDecimal.valueOf(6L))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to calculate money between different currencies");
  }

  @Test
  public void itWillSubtractMonies() {
    assertThat(Money.of(CurrencyCode.USD, BigDecimal.TEN)
        .subtract(Money.of(CurrencyCode.USD, BigDecimal.valueOf(6L))))
        .isEqualTo(Money.of(CurrencyCode.USD, BigDecimal.valueOf(4L)));
  }

  @Test
  public void itWillThrowWhenSubtractingMoniesOfDifferentCurrency() {
    assertThatThrownBy(() -> Money.of(CurrencyCode.USD, BigDecimal.TEN)
        .subtract(Money.of(CurrencyCode.EUR, BigDecimal.valueOf(6L))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to calculate money between different currencies");
  }
}
