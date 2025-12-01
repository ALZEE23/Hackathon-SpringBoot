package com.niagapulse.repository;

import com.niagapulse.model.SalesTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesTransactionRepository extends JpaRepository<SalesTransaction, Long> {
}