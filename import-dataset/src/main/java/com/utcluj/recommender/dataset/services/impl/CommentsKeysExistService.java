package com.utcluj.recommender.dataset.services.impl;

import com.utcluj.recommender.dataset.domain.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

@Service
public class CommentsKeysExistService extends AbstractCachingExistsService<Comment> {

  /** Database template to handle the database operations. */
  private JdbcOperations jdbcTemplate;

  @Override
  public boolean exists(Comment comment) {
    String postId = String.valueOf(comment.getPostId());
    String userId = String.valueOf(comment.getUserId());
    String cacheKey = postId + ":" + userId;

    if (!isCached(cacheKey)) {
      Long postCount =
          jdbcTemplate.queryForObject("select count(*) from posts where id = ?", Long.class, comment.getPostId());
      Long userCount =
          jdbcTemplate.queryForObject("select count(*) from users where id = ?", Long.class, comment.getUserId());

      if (postCount != null && postCount > 0 && userCount != null && userCount > 0) {
        cache(cacheKey);
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
