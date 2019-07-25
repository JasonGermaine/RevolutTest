package com.jasongermaine.revoluttest.mapping;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.jasongermaine.revoluttest.core.Money;
import com.jasongermaine.revoluttest.core.Transfer;
import com.neovisionaries.i18n.CurrencyCode;

public class TransferMapper implements RowMapper<Transfer> {
  @Override
  public Transfer map(ResultSet rs, StatementContext ctx) throws SQLException {
    return Transfer.builder()
        .id(rs.getInt("id"))
        .fromAccountId(rs.getInt("from_account_id"))
        .toAccountId(rs.getInt("to_account_id"))
        .amount(Money.of(
            CurrencyCode.getByCode(rs.getString("currency_code")),
            rs.getBigDecimal("amount")))
        .build();
  }
}
