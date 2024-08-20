package com.reactive.microservice.customerportfolio.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Customer Service: Customer Service is not about making CRUD operations on customers.
 * It is about managing the customer portfolio.
 * */
@RestController
@RequestMapping("/customers")
public class CustomerPortfolioController {

    /* *
     * GET /customers/{customerId} returns CustomerInformation.
     *
     * CustomerInformation (customerId, customerName, balance, List<Holdings>)
     *
     * Holding (ticker, quantity) => Retrieved from Portfolio Item Table.
     *
     * Ticker => APPLE, GOOGLE, AMAZON, MICROSOFT
     *
     * */



    /* *
     * POST /customers/{customerId}/trade received a StockTradeRequest & returns StockTradeResponse
     *
     * StockTradeRequest(ticker, price (current price of the ticker), quantity, traceAction (BUY or SELL)
     *
     * StockTradeResponse(customerId, ticker, price, quantity, traceAction, totalPrice (price * quantity), balance)
     * */

}
