DROP TABLE ksite.user_role;
DROP TABLE ksite.like_dislike;
DROP TABLE ksite.comments;
DROP TABLE ksite.articles;
DROP TABLE ksite.users;
DROP TABLE ksite.tokens;
DROP TABLE ksite.roles;
DROP TABLE ksite.auth_tokens;
DROP TABLE ksite.services;
DROP TABLE ksite.baned_token;

CREATE TABLE ksite.services
(
    id    SERIAL UNIQUE NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    company  VARCHAR(255),
    lore TEXT
);

CREATE TABLE ksite.tokens
(
    id    SERIAL UNIQUE NOT NULL PRIMARY KEY,
    token VARCHAR(255),
    date  TIMESTAMP,
    time INT
);

CREATE TABLE ksite.users
(
    id            SERIAL UNIQUE NOT NULL PRIMARY KEY,
    token         INT,
    username      VARCHAR(255)  NOT NULL UNIQUE,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password      VARCHAR(255)  NOT NULL,
    enabled       BOOLEAN       NOT NULL,
    ban           BOOLEAN       NOT NULL,

    FOREIGN KEY (token) REFERENCES ksite.tokens (id)
);

CREATE TABLE ksite.baned_token
(
    id    SERIAL UNIQUE NOT NULL PRIMARY KEY,
    user_id INT,
    token  VARCHAR(255)
);

CREATE TABLE ksite.auth_tokens
(
    id            SERIAL UNIQUE NOT NULL PRIMARY KEY,
    user_id       INT           NOT NULL,
    service_id    INT           NOT NULL,
    device_id    INT           NOT NULL,
    access_token  text          not null default '',
    refresh_token text          not null default ''
);


CREATE UNIQUE INDEX username_index ON ksite.users (username, email);

CREATE TABLE ksite.roles
(
    id   SERIAL UNIQUE NOT NULL PRIMARY KEY,
    role VARCHAR(30)   NOT NULL UNIQUE

);
CREATE TABLE ksite.user_role
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES ksite.users (id),
    FOREIGN KEY (role_id) REFERENCES ksite.roles (id),

    UNIQUE (user_id, role_id)
);

CREATE TABLE ksite.like_dislike
(
    id      SERIAL UNIQUE NOT NULL PRIMARY KEY,
    user_id INT           NOT NULL,
    article INT           NOT NULL,
    likes   BOOLEAN,
    dislike BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES ksite.users (id)
);

CREATE TABLE ksite.articles
(

    id          SERIAL UNIQUE NOT NULL PRIMARY KEY,
    description TEXT          NOT NULL,
    icon        TEXT          NOT NULL,
    user_id     INT           NOT NULL,
    text        TEXT          NOT NULL,
    hash        VARCHAR(255)  NOT NULL UNIQUE,
    likes       INT,
    dislikes    INT,
    data_create TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES ksite.users (id)
);

CREATE TABLE ksite.comments
(
    id      SERIAL UNIQUE NOT NULL PRIMARY KEY,
    article INT           NOT NULL,
    user_id INT           NOT NULL,
    comment TEXT          NOT NULL,
    FOREIGN KEY (article) REFERENCES ksite.articles (id),
    FOREIGN KEY (user_id) REFERENCES ksite.users (id)

);

CREATE UNIQUE INDEX article_index ON ksite.articles (hash);

INSERT INTO ksite.roles(role)
VALUES ('USER');
INSERT INTO ksite.roles(role)
VALUES ('MODER');
INSERT INTO ksite.roles(role)
VALUES ('ADMIN');
INSERT INTO ksite.roles(role)
VALUES ('EDITOR');


INSERT INTO ksite.services(name, company, lore)
VALUES ('OVERDIFF','UndFrame','Mobile Overcoming Difficulties');
INSERT INTO ksite.services(name, company, lore)
VALUES ('NEEDLE','UndFrame','Mobile needle');


INSERT INTO ksite.users(token, username, email, password, enabled, ban)
VALUES (null, 'undframe', 'undframe@gmail.com',
        '$e0801$rvu1Hsc5uqZbI4hjZfdyx9mKsYDJ7ygUMhikK2SBVN5nx8ktPlYFuweN8xU6c9ev4y+BBRS2WA6rzMbz67pKGw==$pydVEqyfu3t8IMf6UGlkf64NVgT/5Y4zuONvSFYuMXc=',
        true, false);



INSERT INTO ksite.users(token, username, email, password, enabled, ban)
VALUES (null, 'undframe2', 'undframe2@gmail.com',
        '$e0801$rvu1Hsc5uqZbI4hjZfdyx9mKsYDJ7ygUMhikK2SBVN5nx8ktPlYFuweN8xU6c9ev4y+BBRS2WA6rzMbz67pKGw==$pydVEqyfu3t8IMf6UGlkf64NVgT/5Y4zuONvSFYuMXc=',
        true, false);

INSERT INTO ksite.user_role(user_id, role_id)
VALUES (2, 3);

