Feature: Transferring money between accounts

  Scenario: Transferring from one account to another
    Given the following accounts exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 10.00   |
      | account_two | 2           | USD           | 0.00    |
    When I submit transfer:
      | from_account_alias | account_one |
      | to_account_alias   | account_two |
      | amount             | 10.00       |
      | currency_code      | USD         |
    Then the response code is 200
    And the following transfers now exist:
      | from_account_alias | to_account_alias | currency_code | amount |
      | account_one        | account_two      | USD           | 10.00  |
    And the following accounts now exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 0.00    |
      | account_two | 2           | USD           | 10.00   |

  Scenario: Executing sequential transfers
    Given the following accounts exist:
      | alias         | customer_id | currency_code | balance |
      | account_one   | 1           | USD           | 20.00   |
      | account_two   | 2           | USD           | 0.00    |
      | account_three | 12345       | USD           | 10.00   |
    When I submit transfers:
      | from_account_alias | to_account_alias | currency_code | amount |
      | account_one        | account_three    | USD           | 5.00   |
      | account_three      | account_one      | USD           | 2.00   |
    And the following accounts now exist:
      | alias         | customer_id | currency_code | balance |
      | account_one   | 1           | USD           | 17.00   |
      | account_two   | 2           | USD           | 0.00    |
      | account_three | 12345       | USD           | 13.00   |

  Scenario: Disallowing transferring between different currency accounts
    Given the following accounts exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 10.00   |
      | account_two | 2           | EUR           | 0.00    |
    When I submit transfer:
      | from_account_alias | account_one |
      | to_account_alias   | account_two |
      | amount             | 10.00       |
      | currency_code      | USD         |
    Then the response code is 400
    And the following accounts now exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 10.00   |
      | account_two | 2           | EUR           | 0.00    |

  Scenario: Disallowing transferring when insufficient balance
    Given the following accounts exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 0.00    |
      | account_two | 2           | USD           | 0.00    |
    When I submit transfer:
      | from_account_alias | account_one |
      | to_account_alias   | account_two |
      | amount             | 10.00       |
      | currency_code      | USD         |
    Then the response code is 400
    And the following accounts now exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 0.00    |
      | account_two | 2           | USD           | 0.00    |

  Scenario: Submitting multiple transfers
    Given the following accounts exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 0.00    |
      | account_two | 2           | USD           | 0.00    |
    When I submit transfer:
      | from_account_alias | account_one |
      | to_account_alias   | account_two |
      | amount             | 10.00       |
      | currency_code      | USD         |
    Then the response code is 400
    And the following accounts now exist:
      | alias       | customer_id | currency_code | balance |
      | account_one | 1           | USD           | 0.00    |
      | account_two | 2           | USD           | 0.00    |


