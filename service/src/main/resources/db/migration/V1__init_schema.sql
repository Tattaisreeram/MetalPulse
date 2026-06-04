CREATE TABLE users (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)   NOT NULL,
    email       VARCHAR(100)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,
    balance     DECIMAL(18,4) NOT NULL DEFAULT 0.0000,
    role        VARCHAR(10)   NOT NULL DEFAULT 'USER',
    created_at  DATETIME      NOT NULL,
    updated_at  DATETIME      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_user_username (username),
    UNIQUE INDEX idx_user_email    (email)
);

CREATE TABLE trades (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    user_id        BIGINT        NOT NULL,
    trade_type     VARCHAR(10)   NOT NULL,
    metal          VARCHAR(10),
    currency       VARCHAR(10),
    weight_unit    VARCHAR(5),
    quantity       DECIMAL(18,6),
    price_per_unit DECIMAL(18,4),
    total_amount   DECIMAL(18,4) NOT NULL,
    status         VARCHAR(10)   NOT NULL DEFAULT 'COMPLETED',
    created_at     DATETIME      NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_trade_user_id    (user_id),
    INDEX idx_trade_created_at (created_at),
    CONSTRAINT fk_trade_user FOREIGN KEY (user_id) REFERENCES users (id)
);
