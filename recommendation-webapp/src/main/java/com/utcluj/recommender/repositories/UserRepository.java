package com.utcluj.recommender.repositories;

import com.utcluj.recommender.domain.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

  String FIND_USER_BY_NAME =
      "select u from User u where u.id in (select max(id) from User where displayName = ?1 group by displayName)";

  @Query(FIND_USER_BY_NAME)
  User findByDisplayName(String displayName);
}
