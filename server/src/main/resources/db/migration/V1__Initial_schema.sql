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

CREATE TABLE Transfer (
    transferId SERIAL PRIMARY KEY,
    clientId INTEGER,
    status VARCHAR(255) NOT NULL,
    transferTimestamp TIMESTAMPTZ NOT NULL,
    endToEndId VARCHAR(255) NOT NULL,
    currencyCode VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    settlementDate DATE NOT NULL,
    debtorFullName VARCHAR(255) NOT NULL,
    debtorIban VARCHAR(255) NOT NULL,
    debtorBic VARCHAR(255) NOT NULL,
    creditorFullName VARCHAR(255) NOT NULL,
    creditorIban VARCHAR(255) NOT NULL,
    creditorBic VARCHAR(255) NOT NULL,
    remittanceInfo VARCHAR(255),
    FOREIGN KEY (clientId) REFERENCES Client(clientId)
);

CREATE TABLE ConversionRate (
    id SERIAL PRIMARY KEY,
    baseCurrency VARCHAR(20) NOT NULL,
    targetCurrency VARCHAR(20) NOT NULL,
    rate NUMERIC(10, 6) NOT NULL,
    FOREIGN KEY (baseCurrency) REFERENCES CurrencyCode(code),
    FOREIGN KEY (targetCurrency) REFERENCES CurrencyCode(code)
);