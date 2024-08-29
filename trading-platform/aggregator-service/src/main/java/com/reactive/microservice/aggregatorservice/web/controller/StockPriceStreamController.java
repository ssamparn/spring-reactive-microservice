package com.reactive.microservice.aggregatorservice.web.controller;

import com.reactive.microservice.aggregatorservice.model.response.StockPriceStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockPriceStreamController {

    /* *
     * GET call will be made by Browser to get a stream of stock price events.
     * Stock price events comes from Stock service which emits stock price changes periodically.
     * This controller of the aggregator service will consume stock price events (coming from Stock service) and expose this as SSE for the customers for every stock.
     *
     * GET http://localhost:8080/stock/price-stream returns Flux<StockPriceStream>
     * It calls to remote Stock Service to endpoint http://localhost:7070/stock/price-stream
     * StockPriceStream (Ticker ticker, String price, LocalDateTime time)
     * */

    @GetMapping(value = "/price-stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<StockPriceStream> getStockPriceStream() {
        return Flux.empty();
    }


}
