package com.utcluj.recommender.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

  String UNASWARED_POST_WITH_TAG = "select p from Post p join p.tags t where t.id = ?1 and p.answerCount = 0";

  Page<Post> findAllByPostTypeIdOrderByCreationDateDesc(Integer postTypeId, Pageable pageable);

  @Query(UNASWARED_POST_WITH_TAG)
  List<Post> findByTagId(long id, Pageable pageable);
}
