package com.utcluj.recommender.dataset.batch.processors;


import com.utcluj.recommender.dataset.services.ExistsService;

import org.springframework.batch.item.ItemProcessor;

public class ExistsItemProcessor<T> implements ItemProcessor<T, T> {

  private ExistsService service;

  public ExistsItemProcessor(ExistsService service) {
    this.service = service;
  }

  @Override
  public T process(T item) throws Exception {
    if (service.exists(item)) {
      return item;
    } else {
      return null;
    }
  }
}
