package com.utcluj.recommender.service;

import com.utcluj.recommender.domain.Post;
import com.utcluj.recommender.domain.Tag;
import com.utcluj.recommender.repositories.PostRepository;

import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RecommendationService {

  @Autowired
  protected ItemBasedRecommender recomender;

  @Autowired
  protected PostRepository postRepository;

  public List<Post> recommendUnansweredPostsByParentsTags(Post parentPost) throws Exception {
    List<RecommendedItem> items = new ArrayList<>();
    for (Tag tag : parentPost.getTags()) {
      items.addAll(recomender.mostSimilarItems(tag.getId(), 10));
    }
    return retrieveUnansweredPostsForItems(items);
  }

  public List<Post> recommendUnansweredPostsForTags(Set<Tag> tags) throws Exception {
    List<RecommendedItem> items = new ArrayList<>();
    for (Tag tag : tags) {
      items.addAll(recomender.mostSimilarItems(tag.getId(), 10));
    }

    return retrieveUnansweredPostsForItems(items);
  }

  public Set<Post> shuffleAndCut(List<Post> matchingPosts, int nrOfRecommendations) {
    if (matchingPosts.size() > nrOfRecommendations) {
      Collections.shuffle(matchingPosts);
      return new HashSet<>(matchingPosts.subList(0, nrOfRecommendations));
    }
    return new HashSet<>(matchingPosts);
  }

  private List<Post> retrieveUnansweredPostsForItems(List<RecommendedItem> similarItems) {
    List<Post> unansweredMatchingPosts = new ArrayList<>();
    for (RecommendedItem item : similarItems) {
      List<Post> posts = postRepository.findUnansweredByTagId(item.getItemID(), new PageRequest(0, 3));
      unansweredMatchingPosts.addAll(posts);
    }
    return unansweredMatchingPosts;
  }

//  private List<Post> recommendUnansweredPostsByTags(List<Tag> tags) throws Exception {
//    List<RecommendedItem> items = new ArrayList<>();
//    for (Tag tag : tags) {
//      items.addAll(recomender.mostSimilarItems(tag.getId(), 10));
//    }
//    List<Post> matchingPosts = new ArrayList<>();
//    for (RecommendedItem item : items) {
//      List<Post> posts = postRepository.findUnansweredByTagId(item.getItemID(), new PageRequest(0, 3));
//      matchingPosts.addAll(posts);
//    }
//    return matchingPosts;
//  }

//  private List<Post> recommendPostsByUsers(User user) throws Exception {
//    List<RecommendedItem> items = recomender.recommend(user.getId(), 10);
//    List<Post> matchingPosts = new ArrayList<>();
//    for (RecommendedItem item : items) {
//      List<Post> posts = postRepository.findUnansweredByTagId(item.getItemID(), new PageRequest(0, 3));
//      matchingPosts.addAll(posts);
//    }
//    return matchingPosts;
//  }
}
