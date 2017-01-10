package com.utcluj.recommender.dataset.batch.configurations;

import com.utcluj.recommender.dataset.batch.processors.ExistsItemProcessor;
import com.utcluj.recommender.dataset.domain.Vote;
import com.utcluj.recommender.dataset.services.impl.PostExistsService;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.castor.CastorMarshaller;

import java.io.IOException;

import javax.sql.DataSource;

/**
 * Class to configure the votes related reader and writer.
 */
@Configuration
public class VoteConfiguration {

  private PostExistsService postExistsService;

  @Bean
  @StepScope
  public ItemStreamReader<Vote> getVoteItemReader(@Value("#{jobParameters[importDirectory]}") String importDirectory)
      throws IOException {
    CastorMarshaller marshaller = new CastorMarshaller();
    marshaller.setTargetClass(Vote.class);
    marshaller.setMappingLocation(new ClassPathResource("mappings/votesMapping.xml"));
    marshaller.afterPropertiesSet();

    StaxEventItemReader<Vote> voteReader = new StaxEventItemReader<>();
    voteReader.setUnmarshaller(marshaller);
    voteReader.setFragmentRootElementName("row");
    voteReader.setResource(new FileSystemResource(importDirectory + "Votes.xml"));
    voteReader.setStrict(true);

    return voteReader;
  }

  @Bean
  public ItemWriter<Vote> getVoteItemWriter(DataSource dataSource) {
    JdbcBatchItemWriter<Vote> writer = new JdbcBatchItemWriter<>();
    writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
    writer.setSql("insert into VOTES "
                  + "(ID, VERSION, POST_ID, VOTE_TYPE, CREATION_DATE) values "
                  + "(:id, 0, :postId, :voteType, :creationDate)");
    writer.setDataSource(dataSource);
    writer.afterPropertiesSet();

    return writer;
  }

  @Bean
  public ItemProcessor<Vote, Vote> getVoteFilterItemProcessor() {
    return new ExistsItemProcessor<>(postExistsService);
  }

  public PostExistsService getPostExistsService() {
    return postExistsService;
  }

  @Autowired
  public void setPostExistsService(PostExistsService postExistsService) {
    this.postExistsService = postExistsService;
  }
}
