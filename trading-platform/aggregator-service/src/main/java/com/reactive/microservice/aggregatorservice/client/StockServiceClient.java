package com.reactive.microservice.aggregatorservice.client;

import com.reactive.microservice.aggregatorservice.domain.Ticker;
import com.reactive.microservice.aggregatorservice.dto.response.StockPriceResponse;
import com.reactive.microservice.aggregatorservice.model.response.StockPriceStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class StockServiceClient {

    private Flux<StockPriceStream> stockPriceStreamFlux;

    private final WebClient webClient;

    public StockServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /* *
     * GET http://localhost:7070/stock/{ticker}
     * */
    public Mono<StockPriceResponse> getStockPrice(Ticker ticker) {
        return this.webClient.get()
                .uri("/stock/{ticker}", ticker)
                .retrieve()
                .bodyToMono(StockPriceResponse.class);
    }

    public Flux<StockPriceStream> getStockPriceStream() {
        if (Objects.isNull(stockPriceStreamFlux)) {
            this.stockPriceStreamFlux = this.getUpdatedStockPriceStream();
        }
        return stockPriceStreamFlux;
    }

    /* *
     * GET http://localhost:7070/stock/price-stream
     * Since this endpoint will keep on emitting events, we will have to make it a hot publisher. Why?
     * The Stock Price change is going to be same for all the customers (subscribers). So we don't have to call the stock service to get the price stream for each and every user.
     * Instead, we can get the price stream once & we can broadcast the same message to all the subscribers (customers).
     * Since we will invoke this method once we made it private
     * */
    private Flux<StockPriceStream> getUpdatedStockPriceStream() {
        return this.webClient.get()
                .uri("/stock/price-stream")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(StockPriceStream.class)
                .cache(1)
                .retryWhen(retrySpec()); // retry spec is provided to handle error situation.
        // Imagine a scenario in which stock service is restarted, at that point our connection to stock service is broken, and it's emitting error signal.
    }

    private Retry retrySpec() {
        return Retry.fixedDelay(100, Duration.ofSeconds(2))
                .doBeforeRetry(retrySignal -> log.error("stock service price stream call failed. Hence retrying: {}", retrySignal.failure().getMessage()));
    }
}
