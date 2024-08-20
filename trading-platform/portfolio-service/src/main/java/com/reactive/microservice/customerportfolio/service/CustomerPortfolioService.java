package com.reactive.microservice.customerportfolio.service;

import org.springframework.stereotype.Service;

@Service
public class CustomerPortfolioService {

    /* *
     * Trade Action BUY or SELL
     *    BUY:
     *     - Debit amount from customer balance (update customer table)
     *     - Add an entry in the portfolio_item table (insert a new record of the customer portfolio) if no record found
     *     - Increase the quantity in the portfolio_item table (update if a record is found)
     *
     *    SELL:
     *     - Credit amount in the customer balance (update customer table with the updated balance)
     *     - Decrease or Deduct the quantity sold in the portfolio_item table
     * */
}
