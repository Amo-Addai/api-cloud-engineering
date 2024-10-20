package com.example.demo.configurations;

import com.example.demo.configurations.components.JobCompletionNotificationListener;
import com.example.demo.models.UserItemProcessor;
import com.example.demo.pojo.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
//@EnableBatchProcessing // enable batch operations service
public class BatchConfiguration {

    @Autowired
    public DataSource dataSource; // from javax.sql.DataSource (not jakarta.activation.DataSource)

    /*

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<User> reader() {

        FlatFileItemReader<User> reader =
                new FlatFileItemReader<User>();

        reader.setResource(
                new ClassPathResource("data/sample-csv.csv")
        );
        reader.setLineMapper(
                new DefaultLineMapper<User>() {
                    { // internal unnamed-scope

                        setLineTokenizer(
                                new DelimitedLineTokenizer() {
                                    { // internal unnamed-scope
                                        setNames(
                                                new String[] { "firstName", "lastName" }
                                        );
                                    }
                                }
                        );

                        setFieldSetMapper(
                                new BeanWrapperFieldSetMapper<User>() {
                                    { // internal unnamed-scope
                                        setTargetType(User.class);
                                    }
                                }
                        );

                    }
                }
        );

        return reader;
    }

    */

    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }

    /*

    @Bean
    public JdbcBatchItemWriter<User> writer() {
        JdbcBatchItemWriter<User> writer =
                new JdbcBatchItemWriter<User>();

        writer.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<User>()
        );
        writer.setSql(
                "INSERT INTO USERS (first_name, last_name) "
                + "VALUES (:firstName, :lastName)"
        );
        writer.setDataSource(dataSource);

        return writer;
    }

    @Bean
    public Job importUserJob(
            JobCompletionNotificationListener listener
    ) {
        return jobBuilderFactory.get("importUserJob")
                                .incrementer(new RunIdIncrementer())
                                .listener(listener)
                                .flow(step1())
                                .end()
                                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                                 .<User, User> chunk(10) // generic builder-method call
                                 .reader(reader())
                                 .processor(processor())
                                 .writer(writer())
                                 .build();
    }

    */

}
