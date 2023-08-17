package com.example.spring.batch.config;

import com.example.spring.batch.listener.CustomChunkListener;
import com.example.spring.batch.listener.JobListener;
import com.example.spring.domain.model.Contract;
import com.example.spring.domain.model.ContractProcessed;
import com.example.spring.domain.repository.ContractProcessedRepository;
import com.example.spring.domain.repository.ContractRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

@Configuration
@AllArgsConstructor
public class BatchConfig {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractProcessedRepository contractProcessedRepository;


    @Bean
    public RepositoryItemReader<Contract> reader() {
        return new RepositoryItemReaderBuilder<Contract>()
                .name("start")
                .repository(contractRepository)
                .methodName("findAll")
                .sorts(Collections.singletonMap("policy", Sort.Direction.ASC))
                .build();
    }


    @Bean
    public RepositoryItemWriter<ContractProcessed> writer() {
        return new RepositoryItemWriterBuilder<ContractProcessed>()
                .repository(contractProcessedRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        return new ResourcelessTransactionManager();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomChunkListener customChunkListener) {
        return new StepBuilder("step1",jobRepository)
                .<Contract, ContractProcessed>chunk(5, transactionManager)
                .listener(customChunkListener)
                .reader(reader())
                .processor(contract -> new ContractProcessed(contract.getPolicyId(), contract.getPolicy(),  contract.getPolicySituation(), contract.getPolicyBrand(), contract.getPolicyDate(), contract.isExpired(), contract.isDisabled()))
                .writer(writer())
                .build();
    }

    @Bean
    public Job convertCarToUppercaseJob(Step step1, JobRepository jobRepository , JobListener listener) {
        return new JobBuilder("convertCarToUppercaseJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1)
                .build();
    }

}
