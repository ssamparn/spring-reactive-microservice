package com.reactive.microservice.webfluxplayground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableR2dbcRepositories(basePackages = "com.reactive.microservice.webfluxplayground.${section}")
public class WebfluxPlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxPlaygroundApplication.class, args);
    }

}
