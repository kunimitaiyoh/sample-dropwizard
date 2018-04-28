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

--changeset sample:5
ALTER TABLE users CHANGE mail mail VARCHAR(127) NOT NULL;
ALTER TABLE users ADD UNIQUE (mail);

--changeset sample:6
CREATE TABLE access_tokens (
    id VARCHAR(127) PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    created DATETIME NOT NULL,
    last_access DATETIME NOT NULL
);

--changeset sample:7
ALTER TABLE articles CHANGE user_id user_id INT NOT NULL;
ALTER TABLE comments CHANGE user_id user_id INT NOT NULL;
ALTER TABLE comments CHANGE article_id article_id INT NOT NULL;

--changeset sample:8
ALTER TABLE articles ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE comments ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE comments ADD FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE;

--changeset sample:9
ALTER TABLE access_tokens CHANGE user_id user_id INT NOT NULL;
ALTER TABLE access_tokens ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

--changeset sample:10
CREATE TABLE avatars (
    name VARCHAR(127) PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    data MEDIUMBLOB NOT NULL,
    width INT NOT NULL,
    height INT NOT NULL,
    created DATETIME NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
