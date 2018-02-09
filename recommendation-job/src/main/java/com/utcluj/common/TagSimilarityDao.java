package com.utcluj.common;

import com.utcluj.common.model.TagSimilarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transactional;

@Component
public class TagSimilarityDao {

  private static Logger LOG = LoggerFactory.getLogger(TagSimilarityDao.class);

  private final String
      INSERT_COSINE =
      "insert into tag_to_tag_similarity_cosine (tag_id_a,tag_id_b,similarity) values (?1, ?2, ?3) on duplicate key update similarity=?4";
  private final String
      INSERT_LOGLIKELIHOOD =
      "insert into tag_to_tag_similarity_loglikelihood (tag_id_a,tag_id_b,similarity) values (?,?,?) on duplicate key update similarity=?4";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Transactional
  public void createCosine(final TagSimilarity tagSimilarity) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(INSERT_COSINE);
      ps.setInt(1, tagSimilarity.getTagIdA());
      ps.setInt(2, tagSimilarity.getTagIdB());
      ps.setFloat(3, tagSimilarity.getSimilarity());
      ps.setFloat(4, tagSimilarity.getSimilarity());
      LOG.info(
          "Tag similarity inserted with : " + tagSimilarity.getTagIdA() + " " + tagSimilarity.getTagIdB() + " :" + tagSimilarity.getSimilarity());
      return ps;
    });
  }

  @Transactional
  public void createBatchCosine(final List<TagSimilarity> tagSimilarities) {
    jdbcTemplate.batchUpdate(INSERT_COSINE, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        TagSimilarity tagSimilarity = tagSimilarities.get(i);
        preparedStatement.setInt(1, tagSimilarity.getTagIdA());
        preparedStatement.setInt(2, tagSimilarity.getTagIdB());
        preparedStatement.setFloat(3, tagSimilarity.getSimilarity());
        preparedStatement.setFloat(4, tagSimilarity.getSimilarity());
      }

      @Override
      public int getBatchSize() {
        return tagSimilarities.size();
      }
    });
    LOG.info("Tag similarities batch were inserted successfully!");
  }

//  public void createLogLikelihood(final TagSimilarity tagToTagSimilarity) {
//    jdbcTemplate.update(new PreparedStatementCreator() {
//      @Override
//      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//        PreparedStatement ps = connection.prepareStatement(INSERT_LOGLIKELIHOOD);
//        ps.setInt(1, tagToTagSimilarity.getSimilarityId().getTagIdA());
//        ps.setInt(2, tagToTagSimilarity.getSimilarityId().getTagIdB());
//        ps.setFloat(3, tagToTagSimilarity.getSimilarity());
//        return ps;
//      }
//    });
//    LOG.info("Tag similarity inserted");
//  }
}
