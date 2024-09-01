package com.reactive.microservice.aggregatorservice.util;

import com.reactive.microservice.aggregatorservice.exceptions.ApplicationException;
import com.reactive.microservice.aggregatorservice.model.request.TradeRequest;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {

    public static UnaryOperator<Mono<TradeRequest>> validate() {
        return tradeRequestMono -> tradeRequestMono
                .filter(hasTicker())
                .switchIfEmpty(ApplicationException.missingTicker())
                .filter(hasTradeAction())
                .switchIfEmpty(ApplicationException.missingTradeAction())
                .filter(isValidStockQuantity())
                .switchIfEmpty(ApplicationException.invalidStockQuantity());
    }

    private static Predicate<TradeRequest> hasTicker() {
        return tradeRequest -> Objects.nonNull(tradeRequest.ticker()) && StringUtils.isNotEmpty(tradeRequest.ticker().toString());
    }

    private static Predicate<TradeRequest> isValidStockQuantity() {
        return tradeRequest -> Objects.nonNull(tradeRequest.quantity()) && tradeRequest.quantity() > 0;
    }

    private static Predicate<TradeRequest> hasTradeAction() {
        return tradeRequest -> Objects.nonNull(tradeRequest.tradeAction()) && StringUtils.isNotEmpty(tradeRequest.tradeAction().toString());
    }
}
