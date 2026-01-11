package com.debezium.debezium_master_slave.repository;


import com.debezium.debezium_master_slave.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}