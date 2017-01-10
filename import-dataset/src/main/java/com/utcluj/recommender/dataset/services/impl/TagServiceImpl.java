package com.utcluj.recommender.dataset.services.impl;

import com.utcluj.recommender.dataset.services.TagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;

/**
 * Implementation of the TagService interface.
 */
@Service
public class TagServiceImpl implements TagService {

  /** Database template to handle the database operations. */
  private JdbcOperations template;

  @Override
  @Cacheable("tag")
  public long getTagId(final String tag) {
    long id = -1;

    try {
      id = template.queryForObject("select id from TAGS where TAG = ?", Long.class, tag);
    } catch (EmptyResultDataAccessException ignore) {
    }

    if (id < 0) {
      KeyHolder keyHolder = new GeneratedKeyHolder();

      template.update(connection -> {
        PreparedStatement ps =
            connection.prepareStatement("insert into TAGS (VERSION, TAG) VALUES (1, ?)", new String[]{"ID"});
        ps.setString(1, tag);
        return ps;
      }, keyHolder);

      id = keyHolder.getKey().longValue();
    }

    return id;
  }

  @Autowired
  public void setTemplate(JdbcOperations template) {
    this.template = template;
  }
}
