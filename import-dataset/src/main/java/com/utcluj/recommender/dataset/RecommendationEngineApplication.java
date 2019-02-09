package com.utcluj.recommender.dataset;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableBatchProcessing
@EnableCaching
public class RecommendationEngineApplication {

  public static void main(String[] args) {
    SpringApplication.run(RecommendationEngineApplication.class, args);
  }
}
