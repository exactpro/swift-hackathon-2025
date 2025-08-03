INSERT INTO CurrencyCode (code) VALUES
    ('USD'),
    ('EUR'),
    ('USDC');

INSERT INTO Token (currencyCode, address) VALUES
    ('USDC', '345cA3e014Aaf5dcA488057592ee47305D9B3e10');

WITH new_client AS (
    INSERT INTO Client (fullName)
    VALUES ('Marcus Vellon')
    RETURNING clientId
)
INSERT INTO Account (iban, currencyCode, balance, clientId)
SELECT accounts.*, nc.clientId
FROM new_client nc,
     (VALUES
         ('GB33BUKB20201555555551', 'USD', 5000.00),
         ('GB33BUKB20201555555552', 'EUR', 2500.00),
         ('GB33BUKB20201555555553', 'USDC', 10000.00),
         ('GB33BUKB20201555555554', 'USD', 5000.00),
         ('GB33BUKB20201555555555', 'EUR', 2500.00),
         ('GB33BUKB20201555555556', 'USDC', 10000.00)
     ) AS accounts(iban, currencyCode, balance);

WITH new_client AS (
    INSERT INTO Client (fullName)
    VALUES ('Lena Brightfield')
    RETURNING clientId
)
INSERT INTO Account (iban, currencyCode, balance, clientId)
SELECT accounts.*, nc.clientId
FROM new_client nc,
     (VALUES
         ('GE60NB0000000123456781', 'USD', 5000.00),
         ('GE60NB0000000123456782', 'EUR', 2500.00),
         ('GE60NB0000000123456783', 'USDC', 10000.00),
         ('GE60NB0000000123456784', 'USD', 5000.00),
         ('GE60NB0000000123456785', 'EUR', 2500.00),
         ('GE60NB0000000123456786', 'USDC', 10000.00)
     ) AS accounts(iban, currencyCode, balance);

INSERT INTO ConversionRate (baseCurrency, targetCurrency, rate) VALUES
('USD', 'EUR', 0.88),
('EUR', 'USD', 1.14),
('USD', 'USDC', 1.00),
('USDC', 'USD', 1.00),
('EUR', 'USDC', 1.14),
('USDC', 'EUR', 0.88);