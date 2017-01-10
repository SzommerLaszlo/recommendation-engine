package com.utcluj.recommender.dataset.batch.configurations;

import com.utcluj.recommender.dataset.batch.processors.ExistsItemProcessor;
import com.utcluj.recommender.dataset.batch.writers.PostItemWriter;
import com.utcluj.recommender.dataset.domain.Post;
import com.utcluj.recommender.dataset.services.impl.PostOwnerExistsService;

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

/**
 * Class to configure the post related reader and writer.
 */
@Configuration
public class PostConfiguration {

  private PostOwnerExistsService postOwnerExistsService;

  @Bean
  @StepScope
  public ItemStreamReader<Post> getPostItemReader(@Value("#{jobParameters[importDirectory]}") String importDirectory)
      throws IOException {
    CastorMarshaller marshaller = new CastorMarshaller();
    marshaller.setTargetClass(Post.class);
    marshaller.setMappingLocation(new ClassPathResource("mappings/postMapping.xml"));
    marshaller.afterPropertiesSet();

    StaxEventItemReader<Post> postReader = new StaxEventItemReader<>();
    postReader.setUnmarshaller(marshaller);
    postReader.setFragmentRootElementName("row");
    postReader.setResource(new FileSystemResource(importDirectory + "Posts.xml"));
    postReader.setStrict(true);

//    If we want to limit the number of reads
//    postReader.setMaxItemCount(1000);

    return postReader;
  }

  @Bean
  public ItemWriter<Post> getPostItemWriter(DataSource dataSource) {
    JdbcBatchItemWriter<Post> delegateWriter = new JdbcBatchItemWriter<>();
    delegateWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
    delegateWriter.setSql(
        "insert into posts "
        + "(ID, VERSION, POST_TYPE, ACCEPTED_ANSWER_ID, CREATION_DATE, SCORE, VIEW_COUNT, BODY, OWNER_USER_ID, TITLE, ANSWER_COUNT, COMMENT_COUNT, FAVORITE_COUNT, PARENT_ID) values "
        + "(:id, 0, :postTypeId, :acceptedAnswerId, :creationDate, :score, :viewCount, :body, :ownerUserId, :title, :answerCount, :commentCount, :favoriteCount, :parentId)");
    delegateWriter.setDataSource(dataSource);
    delegateWriter.afterPropertiesSet();

    PostItemWriter writer = new PostItemWriter();
    writer.setDelegateWriter(delegateWriter);

    return writer;
  }

  @Bean
  public ItemProcessor<Post, Post> getPostFilterItemProcessor() {
    return new ExistsItemProcessor<>(postOwnerExistsService);
  }


  public PostOwnerExistsService getPostOwnerExistsService() {
    return postOwnerExistsService;
  }

  @Autowired
  public void setPostOwnerExistsService(PostOwnerExistsService postOwnerExistsService) {
    this.postOwnerExistsService = postOwnerExistsService;
  }
}
