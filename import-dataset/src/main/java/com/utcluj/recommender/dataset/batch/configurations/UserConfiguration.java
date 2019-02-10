package com.utcluj.recommender.dataset.batch.configurations;

import com.utcluj.recommender.dataset.domain.User;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.castor.CastorMarshaller;

import java.io.IOException;

import javax.sql.DataSource;

/**
 * Class to configure the user related reader and writer.
 */
@Configuration
public class UserConfiguration {

  @Bean
  @StepScope
  public ItemStreamReader<User> getUsersItemReader(@Value("#{jobParameters[importDirectory]}") String importDirectory,
                                                   @Value("#{jobParameters[nrOfUsersToImport]}") Long nrOfUsers)
      throws IOException {
    CastorMarshaller marshaller = new CastorMarshaller();
    marshaller.setTargetClass(User.class);
    marshaller.setMappingLocation(new ClassPathResource("mappings/userMapping.xml"));
    marshaller.afterPropertiesSet();

    StaxEventItemReader<User> userReader = new StaxEventItemReader<>();
    userReader.setUnmarshaller(marshaller);
    userReader.setFragmentRootElementName("row");
    if (nrOfUsers > 0) {
      userReader.setMaxItemCount(nrOfUsers.intValue());
    }
    userReader.setResource(new FileSystemResource(importDirectory + "Users.xml"));
    userReader.setStrict(true);
    return userReader;
  }

  @Bean
  public ItemWriter<User> getUsersItemWriter(DataSource dataSource) {
    JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
    writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
    writer.setSql(
        "insert into users "
        + "(ID, VERSION, REPUTATION, CREATION_DATE, DISPLAY_NAME, LAST_ACCESS_DATE, LOCATION, ABOUT, VIEWS, UP_VOTES, DOWN_VOTES) values "
        + "(:id, 0, :reputation, :creationDate, :displayName, :lastAccessDate, :location, :about, :views, :upVotes, :downVotes)");
    writer.setDataSource(dataSource);
    writer.afterPropertiesSet();

    return writer;
  }
}
