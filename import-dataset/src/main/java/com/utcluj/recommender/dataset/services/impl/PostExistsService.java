package com.utcluj.recommender.dataset.services.impl;

import com.utcluj.recommender.dataset.domain.Vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

@Service
public class PostExistsService extends AbstractCachingExistsService<Vote> {

  /** Database template to handle the database operations. */
  private JdbcOperations jdbcTemplate;

  @Override
  public boolean exists(Vote vote) {
    String id = String.valueOf(vote.getPostId());

    if (!isCached(id)) {
      Long count = jdbcTemplate.queryForObject("select count(*) from posts where id = ?", Long.class, vote.getPostId());
      if (count != null && count > 0) {
        cache(id);
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  @Autowired
  public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

}
