package com.jasongermaine.revoluttest.mapping;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.Money;
import com.neovisionaries.i18n.CurrencyCode;

public class AccountMapper implements RowMapper<Account> {
  @Override
  public Account map(ResultSet rs, StatementContext ctx) throws SQLException {
    return Account.builder()
        .id(rs.getInt("id"))
        .customerId(rs.getInt("customer_id"))
        .balance(Money.of(
            CurrencyCode.getByCode(rs.getString("currency_code")),
            rs.getBigDecimal("balance")))
        .build();
  }
}
