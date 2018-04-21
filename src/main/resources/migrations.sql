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

--changeset sample:2
ALTER TABLE users CHANGE id id INT AUTO_INCREMENT;
--rollback ALTER TABLE users CHANGE id id INT;

--changeset sample:3
CREATE TABLE articles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    created DATETIME NOT NULL
);

CREATE TABLE comments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    article_id INT REFERENCES articles(id) ON DELETE CASCADE,
    body TEXT NOT NULL,
    created DATETIME NOT NULL
);
--rollback DROP TABLE articles; DROP TABLE comments;

--changeset sample:4
ALTER TABLE users CHANGE password_digest password_digest CHAR(60) NOT NULL;
--rollback ALTER TABLE users CHANGE password_digest password_digest CHAR(64) NOT NULL;
