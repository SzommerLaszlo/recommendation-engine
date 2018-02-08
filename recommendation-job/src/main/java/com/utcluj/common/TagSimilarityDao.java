package com.utcluj.common;

import com.utcluj.common.model.TagSimilarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

import javax.transaction.Transactional;

@Component
public class TagSimilarityDao {

  private static Logger LOG = LoggerFactory.getLogger(TagSimilarityDao.class);

  private final String INSERT_COSINE = "insert into tag_to_tag_similarity_cosine (tag_id_a,tag_id_b,similarity) values (?1, ?2, ?3) on duplicate key update similarity=?4";
  private final String INSERT_LOGLIKELIHOOD = "insert into tag_to_tag_similarity_loglikelihood (tag_id_a,tag_id_b,similarity) values (?,?,?) on duplicate key update similarity=?4";

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
      LOG.info("Tag similarity inserted with : " + tagSimilarity.getTagIdA() + " " + tagSimilarity.getTagIdB() + " :" + tagSimilarity.getSimilarity());
      return ps;
    });
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
