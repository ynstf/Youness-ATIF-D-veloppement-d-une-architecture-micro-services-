package com.atif.billingservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import com.atif.billingservice.entities.Bill;
import com.atif.billingservice.entities.ProductItem;
import com.atif.billingservice.feign.CustomerRestClient;
import com.atif.billingservice.feign.ProductRestClient;
import com.atif.billingservice.model.Customer;
import com.atif.billingservice.model.Product;
import com.atif.billingservice.repository.BillRepository;
import com.atif.billingservice.repository.ProductItemRepository;


import java.util.Collection;
import java.util.Date;

import java.util.Random;


@SpringBootApplication
//@EnableFeignClients(basePackages = "com.atif.billingservice.feign")
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BillRepository  billRepository,
                                        ProductItemRepository productItemRepository,
                                        CustomerRestClient customerRestClient,
                                        ProductRestClient productRestClient){

        return args -> {
            Collection<Customer> customers = customerRestClient.getAllCustomers().getContent();
            Collection<Product> products = productRestClient.getAllProducts().getContent();

            customers.forEach(customer -> {
                Bill bill = Bill.builder()
                        .billingDate(new Date())
                        .customerId(customer.getId())
                        .build();
                billRepository.save(bill);
                products.forEach(product -> {
                    ProductItem productItem = ProductItem.builder()
                            .bill(bill)
                            .productId(product.getId())
                            .quantity(1+new Random().nextInt(10))
                            .unitPrice(product.getPrice())
                            .build();
                    productItemRepository.save(productItem);
                });
            });
        };
    }
}
