package com.utcluj.recommender.repositories;

import com.utcluj.recommender.domain.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

  Page<Post> findAllByPostTypeIdOrderByCreationDateDesc(Integer postTypeId, Pageable pageable);

  String UNASWARED_POST_WITH_TAG = "select p from Post p join p.tags t where t.id = ?1 and p.answerCount = 0";
  @Query(UNASWARED_POST_WITH_TAG)
  List<Post> findUnansweredByTagId(long id, Pageable pageable);

  String HOT_TOPICS = "select p from Post p join p.tags t where p.postTypeId = ?1 order by p.viewCount desc";
  @Query(value = HOT_TOPICS)
  List<Post> retrieveHotTopics(Integer postTypeId, Pageable pageable);
}
