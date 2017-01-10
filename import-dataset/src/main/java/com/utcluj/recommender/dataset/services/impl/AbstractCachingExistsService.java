package com.utcluj.recommender.dataset.services.impl;

import com.utcluj.recommender.dataset.services.ExistsService;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCachingExistsService<T> implements ExistsService<T>, ItemStream {

  private Map<String, Boolean> cache;

  protected boolean isCached(String id) {
    return cache.containsKey(id);
  }

  public void cache(String id) {
    cache.put(id, true);
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    cache = new HashMap<>();
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
  }

  @Override
  public void close() throws ItemStreamException {
    cache = null;
  }
}
