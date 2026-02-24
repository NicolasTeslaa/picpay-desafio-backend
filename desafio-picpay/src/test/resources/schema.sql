CREATE TABLE IF NOT EXISTS WALLETS (
                         id BIGINT PRIMARY KEY,
                         full_name VARCHAR(255),
                         cpf VARCHAR(20),
                         email VARCHAR(255),
                         password VARCHAR(255),
                         type INT,
                         balance DECIMAL(19,2)
);
