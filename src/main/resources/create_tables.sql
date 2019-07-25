CREATE TABLE account (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    customer_id INTEGER,
    currency_code  VARCHAR(3) NOT NULL,
    balance DECIMAL(12,2) NOT NULL,
    CONSTRAINT unique_customer_currency_account UNIQUE (customer_id,currency_code)
);

CREATE TABLE transfer (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    from_account_id INTEGER,
    to_account_id INTEGER,
    currency_code  VARCHAR(3) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    CONSTRAINT FK_FROM_ACCOUNT FOREIGN KEY (from_account_id) REFERENCES account(id),
    CONSTRAINT FK_TO_ACCOUNT FOREIGN KEY (to_account_id) REFERENCES account(id)
);
