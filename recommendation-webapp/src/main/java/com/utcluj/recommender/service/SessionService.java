package com.utcluj.recommender.service;

import com.utcluj.recommender.domain.Tag;

import java.util.HashSet;
import java.util.Set;

public class SessionService {

  private Set<Tag> hotTopics;
  private Set<Tag> usersTodaysTags;

  public SessionService() {
    this.hotTopics = new HashSet<>();
    this.usersTodaysTags = new HashSet<>();
  }

  public Set<Tag> retrieveHotTopics() {
    return hotTopics;
  }

  public Set<Tag> retrieUserPreferredTags() {
    return usersTodaysTags;
  }

  public void addToTodaysPreferredTags(Set<Tag> tags) {
    usersTodaysTags.addAll(tags);
  }
}
