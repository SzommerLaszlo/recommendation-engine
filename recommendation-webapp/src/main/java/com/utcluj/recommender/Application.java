package com.utcluj.recommender;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.AllSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.jdbc.MySQLJDBCInMemoryItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
public class Application {

  public static final String MAHOUT_SIMILARITIES = "SELECT tag_id_a, tag_id_b, similarity FROM tag_to_tag_similarity_mahout";
  public static final String COSINE_SIMILARITIES = "SELECT tag_id_a, tag_id_b, similarity FROM tag_to_tag_similarity_cosine";
  public static final String LOGLIKELIHOOD_SIMILARITIES = "SELECT tag_id_a, tag_id_b, similarity FROM tag_to_tag_similarity_loglikelihood";

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  ItemBasedRecommender recommender(DataSource dataSource) throws Exception {
    DataModel dataModel = new MySQLBooleanPrefJDBCDataModel(dataSource);
    ItemSimilarity similarity = new MySQLJDBCInMemoryItemSimilarity(dataSource, MAHOUT_SIMILARITIES);
    AllSimilarItemsCandidateItemsStrategy candidateItemsStrategy = new AllSimilarItemsCandidateItemsStrategy(similarity);
    return new GenericItemBasedRecommender(dataModel, similarity, candidateItemsStrategy, candidateItemsStrategy);
  }
}
