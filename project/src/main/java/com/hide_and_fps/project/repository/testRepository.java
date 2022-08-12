package com.hide_and_fps.project.repository;
import java.util.Map;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hide_and_fps.project.entity.Customer;

import reactor.core.publisher.Flux;

public interface testRepository extends ReactiveCrudRepository<Customer, Long> {

    @Query("SELECT * FROM customer WHERE last_name = :lastname")
    Flux<Customer> findByLastName(String lastName);

}