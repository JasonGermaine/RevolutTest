Feature: Creating a new account

  Scenario: Adding a first account for a customer
    Given the following accounts exist:
      | customer_id | currency_code | balance |
      | 1           | USD           | 10.00   |
    When I add account:
      | customer_id   | 2   |
      | currency_code | USD |
    Then the response code is 200
    And the following accounts now exist:
      | customer_id | currency_code | balance |
      | 1           | USD           | 10.00   |
      | 2           | USD           | 0.00    |

  Scenario: Adding an account in a different currency for a customer
    Given the following accounts exist:
      | customer_id | currency_code | balance |
      | 1           | USD           | 10.00   |
    When I add account:
      | customer_id   | 1   |
      | currency_code | EUR |
    Then the response code is 200
    And the following accounts now exist:
      | customer_id | currency_code | balance |
      | 1           | USD           | 10.00   |
      | 1           | EUR           | 0.00    |

  Scenario: Ensuring a customer doesn't create duplicate currency accounts
    Given the following accounts exist:
      | customer_id | currency_code | balance |
      | 1           | USD           | 10.00   |
    When I add account:
      | customer_id   | 1   |
      | currency_code | USD |
    Then the response code is 400
