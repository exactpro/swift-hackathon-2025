INSERT INTO CurrencyCode (code) VALUES
    ('USD'),
    ('EUR'),
    ('USDC'),
    ('USDT'),
    ('EURC');

INSERT INTO Token (currencyCode, address) VALUES
    ('USDC', '345cA3e014Aaf5dcA488057592ee47305D9B3e10'),
    ('USDT', 'f25186B5081Ff5cE73482AD761DB0eB0d25abfBF'),
    ('EURC', 'F12b5dd4EAD5F743C6BaA640B0216200e89B60Da');

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
         ('GB33BUKB20201555555554', 'USDT', 4000.00),
         ('GB33BUKB20201555555555', 'EURC', 30000.00)
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
         ('GE60NB0000000123456784', 'USDT', 4000.00),
         ('GE60NB0000000123456785', 'EURC', 3000.00)
     ) AS accounts(iban, currencyCode, balance);

INSERT INTO ConversionRate (baseCurrency, targetCurrency, rate) VALUES
('USD', 'EUR', 0.88),
('EUR', 'USD', 1.14),
('USD', 'USDC', 1.00),
('USDC', 'USD', 1.00),
('USD', 'USDT', 1.00),
('USDT', 'USD', 1.00),
('USD', 'EURC', 0.88),
('EURC', 'USD', 1.14),
('EUR', 'USDC', 1.14),
('USDC', 'EUR', 0.88),
('EUR', 'USDT', 1.14),
('USDT', 'EUR', 0.88),
('EUR', 'EURC', 1.00),
('EURC', 'EUR', 1.00),
('USDC', 'USDT', 1.00),
('USDT', 'USDC', 1.00),
('USDC', 'EURC', 0.88),
('EURC', 'USDC', 1.14),
('USDT', 'EURC', 0.88),
('EURC', 'USDT', 1.14);

INSERT INTO BankETHAddress (bic, ethAddress) VALUES
('TESTUKLLXXX', '0xf17f52151EbEF6C7334FAD080c5704D77216b732'),
('TESTGETBXXX', '0xda6c0ca76e69b32c71301356043fb56d702dfb3d');