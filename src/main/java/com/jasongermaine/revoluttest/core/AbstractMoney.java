package com.jasongermaine.revoluttest.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.neovisionaries.i18n.CurrencyCode;

@Value.Immutable
@Value.Style(typeAbstract = {"Abstract*", "*IF"}, typeImmutable = "*")
@JsonSerialize
public abstract class AbstractMoney {
  private static final int MONEY_SCALE = 6;

  public static Money of(CurrencyCode currencyCode) {
    return Money.of(currencyCode, BigDecimal.ZERO);
  }

  @Value.Parameter
  public abstract CurrencyCode getCurrencyCode();

  @Value.Parameter
  @Value.Default
  public BigDecimal getAmount() {
    return BigDecimal.ZERO;
  }

  public Money add(Money toAdd) {
    ensureCurrencyMatch(toAdd);

    return Money.of(getCurrencyCode(), getAmount().add(toAdd.getAmount()));
  }


  public Money subtract(Money toSubtract) {
    ensureCurrencyMatch(toSubtract);

    return Money.of(getCurrencyCode(), getAmount().subtract(toSubtract.getAmount()));
  }

  @Value.Check
  void validate() {
    Preconditions.checkArgument(
        getAmount().compareTo(BigDecimal.ZERO) >= 0,
        "Money must not be of a negative amount");
  }

  @Value.Check
  AbstractMoney normalize() {
    if (getAmount().scale() != MONEY_SCALE) {
      return Money.of(
          getCurrencyCode(),
          getAmount().setScale(MONEY_SCALE, RoundingMode.HALF_UP));
    }

    return this;
  }

  private void ensureCurrencyMatch(Money otherMoney) {
    Preconditions.checkArgument(
        getCurrencyCode() == otherMoney.getCurrencyCode(),
        "Unable to calculate money between different currencies");
  }
}
