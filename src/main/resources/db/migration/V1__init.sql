-- Roller tablosu
CREATE TABLE roles (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE
);

-- Kullanıcılar tablosu
CREATE TABLE users (
    id                  BIGSERIAL PRIMARY KEY,
    username            VARCHAR(100) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    failed_login_count  INT          NOT NULL DEFAULT 0,
    locked_until        TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Kullanıcı-rol ilişki tablosu
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- Başlangıç rolleri
INSERT INTO roles (name) VALUES ('ROLE_CUSTOMER');
INSERT INTO roles (name) VALUES ('ROLE_TELLER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');