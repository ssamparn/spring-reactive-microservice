DROP TABLE IF EXISTS product;

-- Table: product
CREATE TABLE product (
    id int AUTO_INCREMENT primary key,
    description VARCHAR(100),
    price int
);

INSERT INTO product(description, price)
VALUES
    ('iphone 20', 1000),
    ('iphone 18', 750),
    ('ipad', 800),
    ('mac pro', 3000),
    ('apple watch', 400),
    ('macbook air', 1200),
    ('airpods pro', 250),
    ('imac', 2000),
    ('apple tv', 200),
    ('homepod', 300);