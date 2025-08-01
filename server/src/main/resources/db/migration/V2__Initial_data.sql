INSERT INTO CurrencyCode (code) VALUES
    ('USD'),
    ('EUR'),
    ('USDС');

WITH new_client AS (
    INSERT INTO Client (fullName)
    VALUES ('Marcus Vellon')
    RETURNING clientId
)
INSERT INTO Account (iban, clientId, currencyCode, balance)
SELECT accounts.*, nc.clientId
FROM new_client nc,
     (VALUES
         ('GB33BUKB20201555555555', 'USD', 5000.00),
         ('GB33BUKB20201555555555', 'EUR', 2500.00),
         ('GB33BUKB20201555555555', 'USDC', 10000.00),
         ('GB33BUKB20201555555556', 'USD', 5000.00),
         ('GB33BUKB20201555555556', 'EUR', 2500.00),
         ('GB33BUKB20201555555556', 'USDC', 10000.00)
     ) AS accounts(iban, currencyCode, balance);

WITH new_client AS (
    INSERT INTO Client (fullName)
    VALUES ('Lena Brightfield')
    RETURNING clientId
)
INSERT INTO Account (iban, clientId, currencyCode, balance)
SELECT accounts.*, nc.clientId
FROM new_client nc,
     (VALUES
         ('GE60NB0000000123456788', 'USD', 5000.00),
         ('GE60NB0000000123456788', 'EUR', 2500.00),
         ('GE60NB0000000123456788', 'USDC', 10000.00),
         ('GE60NB0000000123456789', 'USD', 5000.00),
         ('GE60NB0000000123456789', 'EUR', 2500.00),
         ('GE60NB0000000123456789', 'USDC', 10000.00)
     ) AS accounts(iban, currencyCode, balance);

INSERT INTO ConversionRate (baseCurrency, targetCurrency, rate) VALUES
('USD', 'EUR', 0.88),
('EUR', 'USD', 1.14),
('USD', 'USDC', 1.00),
('USDC', 'USD', 1.00),
('EUR', 'USDC', 1.14),
('USDC', 'EUR', 0.88);