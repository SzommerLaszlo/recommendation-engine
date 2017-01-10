package com.utcluj.recommender.dataset.batch.configurations;

import com.utcluj.recommender.dataset.batch.processors.ExistsItemProcessor;
import com.utcluj.recommender.dataset.domain.Comment;
import com.utcluj.recommender.dataset.services.impl.CommentsKeysExistService;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
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

@Configuration
public class CommentConfiguration {

  private CommentsKeysExistService commentsKeysExistService;

  @Bean
  @StepScope
  public ItemStreamReader<Comment> getCommentItemReader(@Value("#{jobParameters[importDirectory]}") String importDirectory)
      throws IOException {
    CastorMarshaller marshaller = new CastorMarshaller();
    marshaller.setTargetClass(Comment.class);
    marshaller.setMappingLocation(new ClassPathResource("mappings/commentMapping.xml"));
    marshaller.afterPropertiesSet();

    StaxEventItemReader<Comment> commentReader = new StaxEventItemReader<>();
    commentReader.setUnmarshaller(marshaller);
    commentReader.setFragmentRootElementName("row");
    commentReader.setResource(new FileSystemResource(importDirectory + "Comments.xml"));
    commentReader.setStrict(true);

    return commentReader;
  }

  @Bean
  public ItemWriter<Comment> getCommentItemWriter(DataSource dataSource) {
    JdbcBatchItemWriter<Comment> writer = new JdbcBatchItemWriter<>();
    writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
    writer.setSql("insert into COMMENTS "
                  + "(ID, VERSION, POST_ID, VALUE, CREATION_DATE, USER_ID, SCORE) values "
                  + "(:id, 0, :postId, :value, :creationDate, :userId, :score)");
    writer.setDataSource(dataSource);
    writer.afterPropertiesSet();
    return writer;
  }

  @Bean
  public ItemProcessor<Comment, Comment> getCommentFilterItemProcessor() {
    return new ExistsItemProcessor<>(commentsKeysExistService);
  }

  public CommentsKeysExistService getCommentsKeysExistService() {
    return commentsKeysExistService;
  }

  @Autowired
  public void setCommentsKeysExistService(CommentsKeysExistService commentsKeysExistService) {
    this.commentsKeysExistService = commentsKeysExistService;
  }

}
