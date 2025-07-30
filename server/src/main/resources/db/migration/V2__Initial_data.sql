INSERT INTO CurrencyCode (code) VALUES
    ('USD'),
    ('EUR'),
    ('USDT');

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