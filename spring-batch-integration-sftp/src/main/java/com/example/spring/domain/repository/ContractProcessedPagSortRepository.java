package com.example.spring.domain.repository;

import com.example.spring.domain.model.ContractProcessed;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContractProcessedPagSortRepository extends PagingAndSortingRepository<ContractProcessed,Long> {
}
