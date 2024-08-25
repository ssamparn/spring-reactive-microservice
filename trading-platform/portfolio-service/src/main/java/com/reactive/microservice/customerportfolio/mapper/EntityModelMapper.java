package com.reactive.microservice.customerportfolio.mapper;

import com.reactive.microservice.customerportfolio.domain.Ticker;
import com.reactive.microservice.customerportfolio.entity.Customer;
import com.reactive.microservice.customerportfolio.entity.PortfolioItem;
import com.reactive.microservice.customerportfolio.model.Holding;
import com.reactive.microservice.customerportfolio.model.request.StockTradeRequest;
import com.reactive.microservice.customerportfolio.model.response.CustomerInformationResponse;
import com.reactive.microservice.customerportfolio.model.response.StockTradeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityModelMapper {

    public CustomerInformationResponse toCustomerInformationResponse(Customer customerEntity, List<PortfolioItem> portfolioItemList) {
        return new CustomerInformationResponse(
                customerEntity.getId(),
                customerEntity.getCustomerName(),
                customerEntity.getBalance(),
                portfolioItemList.stream()
                        .map(portfolioItem -> new Holding(portfolioItem.getTicker(), portfolioItem.getQuantity()))
                        .toList()
        );
    }

    public PortfolioItem toPortFolioItem(Integer customerId, Ticker ticker) {
        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setCustomerId(customerId);
        portfolioItem.setTicker(ticker);
        portfolioItem.setQuantity(0);
        return portfolioItem;
    }

    public StockTradeResponse toStockTradeResponse(Integer customerId, StockTradeRequest request, Integer balance) {
        return new StockTradeResponse(
                customerId,
                request.ticker(),
                request.tickerPrice(),
                request.quantity(),
                request.tradeAction(),
                request.totalPrice(),
                balance
        );
    }
}
