package com.utcluj.recommender.service;

import com.utcluj.recommender.domain.Tag;

import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;

public class SessionService {

  private Set<Tag> hotTopics;
  private Set<Tag> usersTodaysTags;
  private Authentication authentication;

  public SessionService() {
    this.hotTopics = new HashSet<>();
    this.usersTodaysTags = new HashSet<>();
  }

  public Set<Tag> retrieveHotTopics() {
    return hotTopics;
  }

  public void addToHotTopics(Set<Tag> tags) {
    this.hotTopics.addAll(tags);
  }

  public Set<Tag> retrieUserPreferredTags() {
    return usersTodaysTags;
  }

  public void addToTodaysPreferredTags(Set<Tag> tags) {
    this.usersTodaysTags.addAll(tags);
  }

  public void setAuthentication(Authentication authentication) {
    this.authentication = authentication;
  }

  public Authentication getAuthentication() {
    return authentication;
  }

  public void resetValues() {
    this.usersTodaysTags = new HashSet<>();
    this.authentication = null;
  }
}
