package com.reactive.microservice.webfluxplayground.controller;

import com.reactive.microservice.webfluxplayground.AbstractTest;
import com.reactive.microservice.webfluxplayground.model.CustomerModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerControllerTest extends AbstractTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void allCustomers() {
        this.webTestClient.get()
                .uri("/customers")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CustomerModel.class)
                .value(customers -> log.info("{}", customers))
                .hasSize(10);
    }

    @Test
    public void paginatedCustomers() {
        this.webTestClient.get()
                .uri("/customers/paginated?page=3&size=2")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(5)
                .jsonPath("$[1].id").isEqualTo(6);
    }

    @Test
    public void customerById() {
        this.webTestClient.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("sam")
                .jsonPath("$.email").isEqualTo("sam@gmail.com");
    }

    @Test
    public void createAndDeleteCustomer() {
        // create
        CustomerModel customerModel = new CustomerModel(null, "marshal", "marshal@gmail.com");

        this.webTestClient.post()
                .uri("/customers")
                .bodyValue(customerModel)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(11)
                .jsonPath("$.name").isEqualTo("marshal")
                .jsonPath("$.email").isEqualTo("marshal@gmail.com");

        // delete
        this.webTestClient.delete()
                .uri("/customers/11")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().isEmpty();
    }

    @Test
    public void updateCustomer() {
        CustomerModel customerModel = new CustomerModel(null, "noel", "noel@gmail.com");
        this.webTestClient.put()
                .uri("/customers/10")
                .bodyValue(customerModel)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.name").isEqualTo("noel")
                .jsonPath("$.email").isEqualTo("noel@gmail.com");
    }

    @Test
    public void customerNotFound() {
        // get
        this.webTestClient.get()
                .uri("/customers/11")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=11] is not found");

        // delete
        this.webTestClient.delete()
                .uri("/customers/11")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=11] is not found");

        // put
        CustomerModel model = new CustomerModel(null, "noel", "noel@gmail.com");
        this.webTestClient.put()
                .uri("/customers/11")
                .bodyValue(model)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=11] is not found");
    }

    @Test
    @Disabled
    public void invalidInput(){
        // missing name
        CustomerModel missingName = new CustomerModel (null, null, "noel@gmail.com");
        this.webTestClient.post()
                .uri("/customers")
                .bodyValue(missingName)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Name is required");

        // missing email
        CustomerModel missingEmail = new CustomerModel(null, "noel", null);
        this.webTestClient.post()
                .uri("/customers")
                .bodyValue(missingEmail)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");

        // invalid email
        CustomerModel invalidEmail = new CustomerModel(null, "noel", "noel");
        this.webTestClient.put()
                .uri("/customers/10")
                .bodyValue(invalidEmail)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

}
