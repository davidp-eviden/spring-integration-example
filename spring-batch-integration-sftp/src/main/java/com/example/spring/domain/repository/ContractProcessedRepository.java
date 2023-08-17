package com.example.spring.domain.repository;

import com.example.spring.domain.model.Contract;
import com.example.spring.domain.model.ContractProcessed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContractProcessedRepository extends CrudRepository<ContractProcessed,Long> {
}
