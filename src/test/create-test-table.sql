BEGIN;

DROP TABLE IF EXISTS users CASCADE;

DROP TABLE IF EXISTS products CASCADE;

DROP TABLE IF EXISTS cards CASCADE;

DROP TABLE IF EXISTS users_products CASCADE;

CREATE TABLE users (id BIGSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL);

INSERT INTO users (name)
VALUES
('Bill'),
('Jack'),
('Kevin'),
('Michael'),
('Ann');

CREATE TABLE products
(
id BIGSERIAL PRIMARY KEY,
title VARCHAR(150) NOT NULL,
price INTEGER
);

INSERT INTO products (title, price)
VALUES
('Milk', 80),
('Cheese', 200),
('Bread', 60),
('Pasta', 70),
('Eggs', 90);

CREATE TABLE cards
(
id BIGSERIAL PRIMARY KEY,
title VARCHAR(150) NOT NULL,
number VARCHAR(200) NOT NULL,
fk_cards_users BIGINT,
FOREIGN KEY (fk_cards_users) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO cards(title, number, fk_cards_users)
VALUES
('VTB', '123 321', 1),
('SBER', '235 211', 1),
('TINKOFF','542 243',2),
('TINKOFF', '233 712', 1),
('VTB','236 213', 3),
('VTB','213 217',4);

CREATE TABLE users_products
(
user_id BIGINT,
product_id BIGINT,
FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

INSERT INTO users_products (user_id, product_id)
VALUES
(3,1),
(2,3),
(3,5),
(4,2),
(5,3),
(2,1);

COMMIT;