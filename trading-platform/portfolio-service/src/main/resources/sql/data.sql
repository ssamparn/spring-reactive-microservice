DROP TABLE IF EXISTS portfolio_item;
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
    id int AUTO_INCREMENT primary key,
    customer_name VARCHAR(50),
    balance int
);

CREATE TABLE portfolio_item (
    id int AUTO_INCREMENT primary key,
    customer_id int, -- customer_id can be 1, 2, 3
    ticker VARCHAR(10), -- ticker is basically stock name e.g: GOOGLE, APPLE, AMAZON, MICROSOFT. We will consider these 4 stocks only.
    quantity int,
    foreign key (customer_id) references customer(id)
);

insert into customer(customer_name, balance)
    values
        ('Sam', 10000),
        ('Mike', 10000),
        ('John', 10000);