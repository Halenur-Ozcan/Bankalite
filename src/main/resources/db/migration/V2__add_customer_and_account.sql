-- Müşteri tablosu
CREATE TABLE customers (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    segment         VARCHAR(20) NOT NULL DEFAULT 'STANDARD',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Hesap tablosu
CREATE TABLE accounts (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     BIGINT NOT NULL REFERENCES customers(id),
    iban            VARCHAR(26) NOT NULL UNIQUE,
    currency        VARCHAR(3) NOT NULL DEFAULT 'TRY',
    balance         NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);