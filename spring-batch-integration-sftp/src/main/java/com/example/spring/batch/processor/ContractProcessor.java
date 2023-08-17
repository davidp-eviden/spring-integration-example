package com.example.spring.batch.processor;

import com.example.spring.domain.model.Contract;
import org.springframework.batch.item.ItemProcessor;

public class ContractProcessor implements ItemProcessor<Contract,Contract> {
    @Override
    public Contract process(Contract item) throws Exception {
        return item;
    }
}
