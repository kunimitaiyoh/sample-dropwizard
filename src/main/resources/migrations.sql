--liquibase formatted sql

--changeset sample:1
CREATE TABLE users (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    mail TEXT NOT NULL,
    password_digest CHAR(64) NOT NULL,
    created DATETIME NOT NULL
);

--rollback DROP TABLE users;
