package com.example.spring.domain.repository;

import com.example.spring.domain.model.Contract;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContractRepository extends PagingAndSortingRepository<Contract,Long> {
}
