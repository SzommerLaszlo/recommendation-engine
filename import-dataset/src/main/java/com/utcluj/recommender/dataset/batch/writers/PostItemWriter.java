package com.utcluj.recommender.dataset.batch.writers;


import com.utcluj.recommender.dataset.domain.Post;
import com.utcluj.recommender.dataset.services.TagService;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Writer to handle the post related operations and validations.
 */
public class PostItemWriter implements ItemWriter<Post> {

  /** Writer to delegate to. */
  private ItemWriter<Post> postItemWriter;
  /** Database template to handle the database operations. */
  private JdbcOperations jdbcTemplate;
  /** Service for making the relations between tags and the post. */
  private TagService tagService;

  @Override
  public void write(List<? extends Post> items) throws Exception {
    final List<Tuple<Long, Long>> postTagPairings = new ArrayList<>();

    for (Post post : items) {
      if (StringUtils.hasText(post.getTags())) {
        post.setTagIds(new ArrayList<>());

        String[] tags = post.getTags().split(">");

        for (String tag : tags) {
          String curTag = tag;

          if (tag.startsWith("<")) {
            curTag = tag.substring(1);
          }

          long tagId = tagService.getTagId(curTag);
          post.getTagIds().add(tagId);
          postTagPairings.add(new Tuple<>(post.getId(), tagId));
        }
      }
    }

    postItemWriter.write(items);

    jdbcTemplate.batchUpdate("insert into POST_TAG (POST_ID, TAG_ID) VALUES (?, ?)",
                             new BatchPreparedStatementSetter() {
                               @Override
                               public void setValues(PreparedStatement ps, int i) throws SQLException {
                                 ps.setLong(1, postTagPairings.get(i).getKey());
                                 ps.setLong(2, postTagPairings.get(i).getValue());
                               }

                               @Override
                               public int getBatchSize() {
                                 return postTagPairings.size();
                               }
                             });
  }

  public void setDelegateWriter(ItemWriter<Post> postItemWriter) {
    Assert.notNull(postItemWriter);
    this.postItemWriter = postItemWriter;
  }

  @Autowired
  public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Autowired
  public void setTagService(TagService tagService) {
    this.tagService = tagService;
  }

  private class Tuple<T, D> {
    private final T key;
    private final D value;

    public Tuple(T key, D value) {
      this.key = key;
      this.value = value;
    }

    public T getKey() {
      return key;
    }

    public D getValue() {
      return value;
    }

    @Override
    public String toString() {
      return "key: " + key + " value: " + value;
    }
  }
}
