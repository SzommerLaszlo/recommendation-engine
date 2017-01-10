package com.utcluj.recommender.dataset.batch;

import com.utcluj.recommender.dataset.batch.configurations.CommentConfiguration;
import com.utcluj.recommender.dataset.batch.configurations.PostConfiguration;
import com.utcluj.recommender.dataset.batch.configurations.UserConfiguration;
import com.utcluj.recommender.dataset.batch.configurations.VoteConfiguration;
import com.utcluj.recommender.dataset.constants.ConfigVariables;
import com.utcluj.recommender.dataset.domain.Comment;
import com.utcluj.recommender.dataset.domain.Post;
import com.utcluj.recommender.dataset.domain.User;
import com.utcluj.recommender.dataset.domain.Vote;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

  /**
   * Logger instance used within the current class.
   */
  private static final Logger LOGGER = LogManager.getLogger(BatchConfiguration.class);

  private JobBuilderFactory jobs;

  private StepBuilderFactory steps;

  private UserConfiguration userConfiguration;
  private PostConfiguration postConfiguration;
  private VoteConfiguration voteConfiguration;
  private CommentConfiguration commentConfiguration;

  @Bean
  CacheManager cacheManager() {
    SimpleCacheManager manager = new SimpleCacheManager();
    List<Cache> caches = new ArrayList<>();
    caches.add(new ConcurrentMapCache("tag"));
    manager.setCaches(caches);
    return manager;
  }

  /**
   * All fo the configurations used by spring batch.
   */
  @Bean
  protected Properties jobParameters() {
    return new JobParametersBuilder()
        .addString("importDirectory", ConfigVariables.PATH_TO_DATASET)
        .addLong("nrOfUsersToImport", (long) ConfigVariables.NUMBER_OF_USERS_TO_IMPORT)
        .addString("allowReimport", String.valueOf(ConfigVariables.ALLOW_REIMPORT_OF_USERS))
        .toJobParameters()
        .toProperties();
  }

  //  Defining the steps of the spring batch job.
  @Bean
  protected Step importUsers(DataSource dataSource,
                             @Value("#{jobParameters[allowReimport]}") Boolean allowReimport) throws Exception {
    return this.steps.get("step1-importUsers")
        .<User, User>chunk(10000)
        .reader(userConfiguration.getUsersItemReader(null, null))
        .writer(userConfiguration.getUsersItemWriter(dataSource))
        .allowStartIfComplete(allowReimport)
        .build();
  }

  @Bean
  protected Step importPosts(DataSource dataSource,
                             @Value("#{jobParameters[allowReimport]}") Boolean allowReimport) throws Exception {
    return this.steps.get("step2-importPosts")
        .<Post, Post>chunk(10000)
        .reader(postConfiguration.getPostItemReader(null))
        .processor(postConfiguration.getPostFilterItemProcessor())
        .writer(postConfiguration.getPostItemWriter(dataSource))
        .stream(postConfiguration.getPostOwnerExistsService())
        .allowStartIfComplete(allowReimport)
        .build();
  }

  @Bean
  protected Step importVotes(DataSource dataSource,
                             @Value("#{jobParameters[allowReimport]}") Boolean allowReimport) throws IOException {
    return this.steps.get("step3-importVotes")
        .<Vote, Vote>chunk(10000)
        .reader(voteConfiguration.getVoteItemReader(null))
        .processor(voteConfiguration.getVoteFilterItemProcessor())
        .writer(voteConfiguration.getVoteItemWriter(dataSource))
        .stream(voteConfiguration.getPostExistsService())
        .allowStartIfComplete(allowReimport)
        .build();
  }

  @Bean
  protected Step importComments(DataSource dataSource,
                                @Value("#{jobParameters[allowReimport]}") Boolean allowReimport) throws IOException {
    return this.steps.get("step4-importComments")
        .<Comment, Comment>chunk(10000)
        .reader(commentConfiguration.getCommentItemReader(null))
        .processor(commentConfiguration.getCommentFilterItemProcessor())
        .writer(commentConfiguration.getCommentItemWriter(dataSource))
        .stream(commentConfiguration.getCommentsKeysExistService())
        .allowStartIfComplete(allowReimport)
        .build();
  }

  @Bean
  public Job job(DataSource dataSource, JobCompletionListener listener) throws Exception {
    Flow importVotesFlow = new FlowBuilder<Flow>("importVotesFlow").start(importVotes(dataSource, null)).end();
    Flow importCommentsFlow = new FlowBuilder<Flow>("importCommentsFlow").start(importComments(dataSource, null)).end();
    LOGGER.info("The used job parameters are: " + jobParameters());
    return new FlowJobBuilder(this.jobs.get("import"))
        .start(importUsers(dataSource, null))
        .next(importPosts(dataSource, null))
        .next(importVotesFlow)
        .next(importCommentsFlow)
        .end()
        .listener(listener)
        .build();
  }

  @Autowired
  public void setJobs(JobBuilderFactory jobs) {
    this.jobs = jobs;
  }

  @Autowired
  public void setSteps(StepBuilderFactory steps) {
    this.steps = steps;
  }

  @Autowired
  public void setUserConfiguration(UserConfiguration userConfiguration) {
    this.userConfiguration = userConfiguration;
  }

  @Autowired
  public void setPostConfiguration(PostConfiguration postConfiguration) {
    this.postConfiguration = postConfiguration;
  }

  @Autowired
  public void setVoteConfiguration(VoteConfiguration voteConfiguration) {
    this.voteConfiguration = voteConfiguration;
  }

  @Autowired
  public void setCommentConfiguration(
      CommentConfiguration commentConfiguration) {
    this.commentConfiguration = commentConfiguration;
  }
}
