CREATE SEQUENCE IF NOT EXISTS product_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE products
(
    product_id   BIGINT       NOT NULL,
    name         VARCHAR(255) NOT NULL,
    price        DECIMAL      NOT NULL,
    stock_status VARCHAR(20)  NOT NULL,
    stock_count  INT          NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (product_id)
);