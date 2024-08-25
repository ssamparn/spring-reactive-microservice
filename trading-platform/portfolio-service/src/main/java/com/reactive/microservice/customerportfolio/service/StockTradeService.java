package com.reactive.microservice.customerportfolio.service;

import com.reactive.microservice.customerportfolio.domain.TradeAction;
import com.reactive.microservice.customerportfolio.entity.Customer;
import com.reactive.microservice.customerportfolio.entity.PortfolioItem;
import com.reactive.microservice.customerportfolio.exceptions.ApplicationException;
import com.reactive.microservice.customerportfolio.mapper.EntityModelMapper;
import com.reactive.microservice.customerportfolio.model.request.StockTradeRequest;
import com.reactive.microservice.customerportfolio.model.response.StockTradeResponse;
import com.reactive.microservice.customerportfolio.repository.CustomerPortfolioRepository;
import com.reactive.microservice.customerportfolio.repository.PortfolioItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
public class StockTradeService {

    private final CustomerPortfolioRepository customerPortfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final EntityModelMapper entityModelMapper;

    /* *
     * Trade Action
     *    BUY:
     *     - If the customer has enough balance (retrieved from customer table), BUY will be executed.
     *     - Debit amount from customer balance (update customer table)
     *     - Add an entry in the portfolio_item table (insert a new record of the customer portfolio) if no record found
     *     - Increase the quantity in the portfolio_item table (update if a record is found)
     * */

    @Transactional
    public Mono<StockTradeResponse> trade(Integer customerId, StockTradeRequest stockTradeRequest) {
        TradeAction tradeAction = stockTradeRequest.tradeAction();

        return switch (tradeAction) {
            case BUY -> this.buyStock(customerId, stockTradeRequest);
            case SELL -> this.sellStock(customerId, stockTradeRequest);
        };

    }

    private Mono<StockTradeResponse> buyStock(Integer customerId, StockTradeRequest stockTradeRequest) {
        Mono<Customer> validatedCustomerMono = this.customerPortfolioRepository.findById(customerId)
                .switchIfEmpty(ApplicationException.customerNotFound(customerId))
                .filter(customer -> customer.getBalance() >= stockTradeRequest.totalPrice()) // check if the customer has enough balance to BUY the stock.
                .switchIfEmpty(ApplicationException.insufficientBalance(customerId));

        Mono<PortfolioItem> portfolioItemMono = portfolioItemRepository.findByCustomerIdAndTicker(customerId, stockTradeRequest.ticker()) // check if the customer owns the requested stock (ticker).
                .defaultIfEmpty(entityModelMapper.toPortFolioItem(customerId, stockTradeRequest.ticker())); // default entity value that will be newly inserted to portfolio item table if the customer does not have that stock (ticker).

        return validatedCustomerMono.zipWhen(customer -> portfolioItemMono) // same as validatedCustomerMono.zipWhen(customer -> portfolioItemMono, Tuples::of)
                .flatMap(response -> executeBuy(response.getT1(), response.getT2(), stockTradeRequest));
    }

    private Mono<StockTradeResponse> executeBuy(Customer customer, PortfolioItem portfolioItem, StockTradeRequest stockTradeRequest) {
        customer.setBalance(customer.getBalance() - stockTradeRequest.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() + stockTradeRequest.quantity());

        StockTradeResponse stockTradeResponse = entityModelMapper.toStockTradeResponse(customer.getId(), stockTradeRequest, customer.getBalance());

        return Mono.zip(this.customerPortfolioRepository.save(customer), this.portfolioItemRepository.save(portfolioItem))
                .thenReturn(stockTradeResponse);
    }

    /* *
     * Trade Action
     *    SELL:
     *     - If the customer owns the stock (portfolio_item table has the customer stock information), SELL will be executed.
     *     - Credit amount in the customer balance (update customer table with the updated balance)
     *     - Decrease or Deduct the quantity sold in the portfolio_item table
     * */

    private Mono<StockTradeResponse> sellStock(Integer customerId, StockTradeRequest stockTradeRequest) {
        Mono<Customer> validatedCustomerMono = this.customerPortfolioRepository.findById(customerId)
                .switchIfEmpty(ApplicationException.customerNotFound(customerId));

        Mono<PortfolioItem> portfolioItemMono = this.portfolioItemRepository.findByCustomerIdAndTicker(customerId, stockTradeRequest.ticker())
                .filter(portfolioItem -> portfolioItem.getQuantity() >= stockTradeRequest.quantity())
                .switchIfEmpty(ApplicationException.insufficientShares(customerId));

        return validatedCustomerMono.zipWhen(customer -> portfolioItemMono, (Tuples::of)) // Here (Tuples::of) is a method reference for (customer, portfolioItem) -> Tuples.of(customer, portfolioItem). This Tuples.of() is optional & provided here to make things explicit.
                .flatMap(tuple -> executeSell(tuple.getT1(), tuple.getT2(), stockTradeRequest));
    }

    private Mono<StockTradeResponse> executeSell(Customer customer, PortfolioItem portfolioItem, StockTradeRequest stockTradeRequest) {
        customer.setBalance(customer.getBalance() + stockTradeRequest.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() - stockTradeRequest.quantity());

        StockTradeResponse stockTradeResponse = entityModelMapper.toStockTradeResponse(customer.getId(), stockTradeRequest, customer.getBalance());

        return Mono.zip(this.customerPortfolioRepository.save(customer), this.portfolioItemRepository.save(portfolioItem))
                .thenReturn(stockTradeResponse);
    }
}