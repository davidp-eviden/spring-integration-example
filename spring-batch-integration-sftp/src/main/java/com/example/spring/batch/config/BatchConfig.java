package com.example.spring.batch.config;

import com.example.spring.batch.listener.CustomChunkListener;
import com.example.spring.batch.listener.CustomJobExecutionListener;
import com.example.spring.batch.listener.CustomStepExecutionListener;
import com.example.spring.domain.model.Contract;
import com.example.spring.domain.model.ContractProcessed;
import com.example.spring.domain.repository.ContractProcessedRepository;
import com.example.spring.domain.repository.ContractRepository;
import com.example.spring.integration.gateway.CustomGateway;
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
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

@Configuration
public class BatchConfig {
    private final String DEFAULT_FILE_PATH = "src/main/resources/data/contracts.csv";
    private final String[] COLUMN_NAMES = new String[]{"policyId", "policy", "policySituation", "policyBrand", "policyDate", "expired", "disabled"};
    private final ContractRepository contractRepository;
    private final ContractProcessedRepository contractProcessedRepository;

    public BatchConfig(ContractRepository contractRepository, ContractProcessedRepository contractProcessedRepository) {
        this.contractRepository = contractRepository;
        this.contractProcessedRepository = contractProcessedRepository;
    }


    // ============================================= JOBS =============================================
    // move the data from contract table to contract_processed table ( in database ).
    @Bean
    public Job moveToOtherTableAndWriteInCsvJob(Step moveToOtherTableStep, JobRepository jobRepository, CustomJobExecutionListener listener, Step convertToCsvStep) {
        return new JobBuilder("moveToOtherTableAndWriteInCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener) // job listener
                .start(moveToOtherTableStep).on("FAILED").fail() // If the moveToOtherTableStep fails - stop  the job
                .from(moveToOtherTableStep).on("COMPLETED").to(convertToCsvStep) // Otherwise continue to the next step
                .from(convertToCsvStep).on("FAILED").fail().end() // If the convertToCsvStep fails stop the job otherwise continue the execution.
                .build();
    }


    // ============================================= STEPS =============================================
    @Bean
    public Step moveToOtherTableStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomChunkListener customChunkListener, CustomStepExecutionListener customStepExecutionListener) {
        return new StepBuilder("moveToOtherTableStep", jobRepository)
                .<Contract, ContractProcessed>chunk(5, transactionManager)
                .listener(customChunkListener) // chunk listener
                .listener(customStepExecutionListener) // step listener
                .reader(reader()) // read from the contract table using repositoryItemReader
                .processor(contract -> new ContractProcessed(contract.getPolicyId(), contract.getPolicy(), contract.getPolicySituation(), contract.getPolicyBrand(), contract.getPolicyDate(), contract.isExpired(), contract.isDisabled()))
                .writer(writer()) // write  to the contract_processed table using repositoryItemWriter
                .build();
    }

    @Bean
    public Step convertToCsvStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomChunkListener chunkListener, CustomStepExecutionListener customStepExecutionListener) {
        return new StepBuilder("convertToCsvStep", jobRepository)
                .<Contract, Contract>chunk(5, transactionManager)
                .listener(chunkListener) // chunk listener
                .listener(customStepExecutionListener) // step listener
                .reader(reader()) // read from the contract table
                .writer(writerToCsv()) // write to the csv file
                .build();
    }


    /*
    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomGateway customGateway){
        return new StepBuilder("fileToSftpStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    customGateway.sendToSftp(new FileSystemResource(DEFAULT_FILE_PATH).getFile());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
     */



    // ============================================= READERS AND WRITERS =============================================
    @Bean
    public RepositoryItemReader<Contract> reader() {
        return new RepositoryItemReaderBuilder<Contract>()
                .name("reader")
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
    public FlatFileItemWriter<Contract> writerToCsv() {
        // Convert an object ( in this case a contract object ) to an array of its parts.
        BeanWrapperFieldExtractor<Contract> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(COLUMN_NAMES);

        return new FlatFileItemWriterBuilder<Contract>()
                .name("writerToCsv")
                .headerCallback(writer -> writer.write(String.join(",", COLUMN_NAMES))) // Set the header values
                .resource(new FileSystemResource(DEFAULT_FILE_PATH)) // Set the output directory
                .delimited()
                .delimiter(",") // Each element is separated by commas.
                .fieldExtractor(beanWrapperFieldExtractor) // Use the extractor ( Convert from contract objet to array of its parts ).
                .build();
    }
}