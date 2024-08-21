package com.reactive.microservice.customerportfolio.service;

import org.springframework.stereotype.Service;

@Service
public class CustomerPortfolioService {

    /* *
     * Trade Action BUY or SELL
     *    BUY:
     *     - If the customer has enough balance (retrieved from customer table), BUY will be executed.
     *     - Debit amount from customer balance (update customer table)
     *     - Add an entry in the portfolio_item table (insert a new record of the customer portfolio) if no record found
     *     - Increase the quantity in the portfolio_item table (update if a record is found)
     *
     *    SELL:
     *     - If the customer owns the stock (portfolio_item table has the customer stock information), SELL will be executed.
     *     - Credit amount in the customer balance (update customer table with the updated balance)
     *     - Decrease or Deduct the quantity sold in the portfolio_item table
     * */
}
