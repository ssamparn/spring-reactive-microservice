//package com.reactive.microservice.webfluxplayground.web.controller;
//
//import com.reactive.microservice.webfluxplayground.exceptions.ApplicationExceptions;
//import com.reactive.microservice.webfluxplayground.model.CustomerCategory;
//import com.reactive.microservice.webfluxplayground.model.CustomerModel;
//import com.reactive.microservice.webfluxplayground.service.CustomerService;
//import com.reactive.microservice.webfluxplayground.validator.RequestValidator;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestAttribute;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("/customers")
//@RequiredArgsConstructor
//public class CustomerController {
//
//    private final CustomerService customerService;
//
//    @GetMapping
//    public Flux<CustomerModel> allCustomers(@RequestAttribute("user-profile") CustomerCategory userProfile) {
//        log.info("User Profile: {}", userProfile);
//        return this.customerService.getAllCustomers();
//    }
//
//    @GetMapping("/paginated")
//    public Mono<List<CustomerModel>> paginatedCustomers(@RequestAttribute("user-profile") CustomerCategory userProfile,
//                                                  @RequestParam(defaultValue = "1") Integer page,
//                                                  @RequestParam(defaultValue = "3") Integer size) {
//        log.info("User Profile: {}", userProfile);
//        return this.customerService.getAllCustomers(page, size)
//                                    .collectList();
//    }
//
//    @GetMapping("/{customerId}")
//    public Mono<CustomerModel> getCustomer(@RequestAttribute("user-profile") CustomerCategory userProfile,
//                                           @PathVariable Integer customerId) {
//        log.info("User Profile: {}", userProfile);
//        return this.customerService.getCustomerById(customerId)
//                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId));
//    }
//
//    @PostMapping
//    public Mono<CustomerModel> saveCustomer(@RequestAttribute("user-profile") CustomerCategory userProfile,
//                                            @RequestBody Mono<CustomerModel> customerModelMono) {
//        log.info("User Profile: {}", userProfile);
//        return customerModelMono.transform(RequestValidator.validate())
//                .as(this.customerService::saveCustomer);
//    }
//
//    @PutMapping("/{customerId}")
//    public Mono<CustomerModel> updateCustomer(@RequestAttribute("user-profile") CustomerCategory userProfile,
//                                              @PathVariable Integer customerId,
//                                              @RequestBody Mono<CustomerModel> customerModelMono) {
//        log.info("User Profile: {}", userProfile);
//        return customerModelMono.transform(RequestValidator.validate())
//                .as(validatedCustomer -> this.customerService.updateCustomerWay2(customerId, validatedCustomer))
//                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId));
//    }
//
//    @DeleteMapping("/{customerId}")
//    public Mono<Void> deleteCustomer(@RequestAttribute("user-profile") CustomerCategory userProfile,
//                                     @PathVariable Integer customerId) {
//        log.info("User Profile: {}", userProfile);
//        return this.customerService.deleteCustomerById(customerId)
//                .filter(deleted -> deleted)
//                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
//                .then();
//    }
//
//}
