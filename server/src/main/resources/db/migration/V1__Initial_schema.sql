CREATE TABLE CurrencyCode (
    code VARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE Token (
    currencyCode VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(42) NOT NULL UNIQUE,
    FOREIGN KEY (currencyCode) REFERENCES CurrencyCode(code)
);

CREATE TABLE Client (
    clientId SERIAL PRIMARY KEY,
    fullName VARCHAR(255) NOT NULL
);

CREATE TABLE Account (
    iban VARCHAR(34) NOT NULL PRIMARY KEY,
    clientId INTEGER NOT NULL,
    currencyCode VARCHAR(20) NOT NULL,
    balance NUMERIC(19, 4) NOT NULL,
    FOREIGN KEY (clientId) REFERENCES Client(clientId),
    FOREIGN KEY (currencyCode) REFERENCES CurrencyCode(code)
);
