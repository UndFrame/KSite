DROP TABLE ksite.user_role;
DROP TABLE ksite.users;
DROP TABLE ksite.tokens;
DROP TABLE ksite.roles;
DROP TABLE ksite.comments;
DROP TABLE ksite.articles;


CREATE TABLE ksite.tokens
(
    id    INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255),
    date  TIMESTAMP
        COLLATE utf8mb4_unicode_ci
);
CREATE TABLE ksite.users
(
    id       INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    token    INT,
    username VARCHAR(255) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled  BIT          NOT NULL,
    ban      BIT          NOT NULL
        COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (token) REFERENCES ksite.tokens (id)
);
CREATE UNIQUE INDEX username_index ON ksite.users (username, email);

CREATE TABLE ksite.roles
(
    id   INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(30) NOT NULL UNIQUE
        COLLATE utf8mb4_unicode_ci
);
CREATE TABLE ksite.user_role
(
    user_id INT NOT NULL,
    role_id INT NOT NULL
        COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (user_id) REFERENCES ksite.users (id),
    FOREIGN KEY (role_id) REFERENCES ksite.roles (id),

    UNIQUE (user_id, role_id)
);

CREATE TABLE ksite.articles
(

    id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    description TEXT         NOT NULL,
    text        TEXT         NOT NULL,
    hash        VARCHAR(255) NOT NULL UNIQUE
        COLLATE utf8mb4_unicode_ci
);

CREATE TABLE ksite.comments
(
    id         INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    article_id INT  NOT NULL,
    comment    TEXT NOT NULL
        COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (article_id) REFERENCES ksite.articles (id)

);

CREATE UNIQUE INDEX article_index ON ksite.articles (hash);



INSERT INTO ksite.roles(role)
VALUES ('USER');
INSERT INTO ksite.roles(role)
VALUES ('MODER');
INSERT INTO ksite.roles(role)
VALUES ('ADMIN');

INSERT INTO ksite.articles(description, text, hash)
VALUES ('article_1', 'sdsadasda', 'article1');
INSERT INTO ksite.articles(description, text, hash)
VALUES ('article_2', 'bbbnsdsnd', 'article2');

INSERT INTO ksite.articles(description, text, hash)
VALUES ('article_3', '<h1>Тест о лохах</h1>', 'article3');

