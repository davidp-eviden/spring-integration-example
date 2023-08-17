package com.example.spring.batch.config;

import com.example.spring.batch.listener.CustomChunkListener;
import com.example.spring.batch.listener.JobListener;
import com.example.spring.domain.model.Contract;
import com.example.spring.domain.model.ContractProcessed;
import com.example.spring.domain.repository.ContractProcessedRepository;
import com.example.spring.domain.repository.ContractRepository;
import jakarta.annotation.Resource;
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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;

import static java.lang.Integer.parseInt;

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
    public FlatFileItemWriter<ContractProcessed> writerCSV() {
        FlatFileItemWriter<ContractProcessed> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("src/main/resources/output/output.csv"));
        writer.setLineAggregator(new DelimitedLineAggregator<ContractProcessed>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<ContractProcessed>() {
                    {
                        setNames(new String[] { "policyId", "policy", "policySituation", "policyBrand", "policyDate", "expired", "disabled"});
                    }
                });
            }
        });
        return writer;
    }


    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomChunkListener customChunkListener) {
        return new StepBuilder("step1",jobRepository)
                .<Contract, ContractProcessed>chunk(5, transactionManager)
                .listener(customChunkListener)
                .reader(reader())
                .processor(contract -> new ContractProcessed(contract.getPolicyId(), contract.getPolicy(),  contract.getPolicySituation().toUpperCase(), contract.getPolicyBrand().toUpperCase(), LocalDateTime.now(), contract.isExpired(), contract.isDisabled()))
                .writer(writer())
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomChunkListener customChunkListener) {
        return new StepBuilder("step2", jobRepository)
                .<Contract, ContractProcessed>chunk(5, transactionManager)
                .listener(customChunkListener)
                .reader(reader())
                .processor(contract -> new ContractProcessed(contract.getPolicyId(), contract.getPolicy() + "/Procesada",  contract.getPolicySituation(), contract.getPolicyBrand(), contract.getPolicyDate(), contract.isExpired(), contract.isDisabled()))
                .writer(writerCSV())
                .build();
    }

    @Bean
    public Job passContractToContractProcessed(Step step1, Step step2, JobRepository jobRepository , JobListener listener) {
        return new JobBuilder("passContractToContractProcessed", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1)
                .next(step2)
                .build();
    }

}
