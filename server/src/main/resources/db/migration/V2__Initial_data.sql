INSERT INTO CurrencyCode (code) VALUES
    ('USD'),
    ('EUR'),
    ('USDС');

WITH new_client AS (
    INSERT INTO Client (fullName)
    VALUES ('Anton Sitnikov')
    RETURNING clientId
)
INSERT INTO Account (iban, clientId, currencyCode, balance)
SELECT 'GB29NWBK60161331926819', clientId, 'USD', 1000.00
FROM new_client;

WITH new_client AS (
    INSERT INTO Client (fullName)
    VALUES ('Tony Stark')
    RETURNING clientId
)
INSERT INTO Account (iban, clientId, currencyCode, balance)
SELECT 'CH7889144788712476784', clientId, 'USD', 5000.00
FROM new_client;

INSERT INTO ConversionRate (baseCurrency, targetCurrency, rate) VALUES
('USD', 'EUR', 0.88),
('EUR', 'USD', 1.14),
('USD', 'USDC', 1.00),
('USDC', 'USD', 1.00),
('EUR', 'USDC', 1.14),
('USDC', 'EUR', 0.88);