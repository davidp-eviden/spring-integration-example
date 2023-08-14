package com.team.batch.config;

import com.team.batch.listener.JobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import com.team.integration.IntegrationConfig;

@Configuration
public class BatchConfig {

    /**
     * This Job ( bean )  is inyected in the {@link IntegrationConfig} class
     * @param jobRepository
     * @param step1
     * @param jobListener
     * @return
     */
    @Bean
    public Job sampleJob(JobRepository jobRepository, Step step1, JobListener jobListener) {
        return new JobBuilder("sampleJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // The job id will increment +1
                .listener(jobListener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<String> fileItemReader) {
        return new StepBuilder("step1", jobRepository)
                .<String, String>chunk(50, transactionManager) //TODO: What is a chunk
                .reader(fileItemReader)
                .processor(String::toUpperCase) // Convert each element from the CSV to Uppercase
                .writer(System.out::println) // Don't do noting only print in console the data.
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> fileItemReader(@Value("#{jobParameters['filePath']}") String resource) {
        return new FlatFileItemReaderBuilder<String>()
                .name("fileItemReader") // The name is obligatory
                .resource(new FileSystemResource(resource))
                .lineMapper(new PassThroughLineMapper())
                .linesToSkip(1) // Skip the header line
                .delimited()
                    .delimiter(",") // Read the csv columns separated by (,)
                    .names(new String[]{"licensePlate,name,price,available"}) // The CSV headers
                .build();
    }

    /*
    @Bean
    public FlatFileItemWriter<String> fileItemWriter(@Value("#{jobParameters['destination']}") String resource){
        return new FlatFileItemWriterBuilder<String>()
                .name("fileItemWriter")
                .resource(new FileSystemResource(resource))
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }

     */

    @Bean //TODO: What does the PlatformTransactionManager
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }
}
