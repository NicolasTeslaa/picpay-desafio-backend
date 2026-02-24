DELETE TRANSACTIONS;
DELETE WALLETS;

INSERT INTO WALLETS (FULL_NAME, CPF, EMAIL, "PASSWORD", "TYPE", BALANCE)
VALUES
    ('João Silva', '12345678901', 'joao@email.com', '123456', 1, 1000.00),
    ('Maria Souza', '98765432100', 'maria@email.com', 'abcdef', 2, 500.00);
