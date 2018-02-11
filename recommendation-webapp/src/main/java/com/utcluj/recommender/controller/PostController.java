package com.utcluj.recommender.controller;

import com.utcluj.recommender.domain.PageWrapper;
import com.utcluj.recommender.domain.Post;
import com.utcluj.recommender.repositories.PostRepository;
import com.utcluj.recommender.domain.Tag;
import com.utcluj.recommender.domain.User;
import com.utcluj.recommender.repositories.UserRepository;
import com.utcluj.recommender.service.SessionService;

import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class PostController {

  @Autowired
  ItemBasedRecommender recommender;

  @Autowired
  PostRepository postRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  SessionService sessionService;

  @RequestMapping("/")
  public String list(Pageable pageable, Model model) {
    Page<Post> curPage = postRepository.findAllByPostTypeIdOrderByCreationDateDesc(1, pageable);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.getName().toLowerCase().contains("anonymous")) {
      Set<Tag> hotTopics = sessionService.retrieveHotTopics();
      model.addAttribute("hotTopics", hotTopics);
    }

    PageWrapper<Post> page = new PageWrapper<>(curPage, "/");
    model.addAttribute("page", page);
    return "post/index";
  }

  @RequestMapping("/post/{id}")
  public String show(@PathVariable long id, Model model) {
    Post curPost = postRepository.findOne(id);

    curPost.setViewCount(curPost.getViewCount() + 1);
    postRepository.save(curPost);

    model.addAttribute("post", curPost);
    if (!model.containsAttribute("recommendations")) {
      model.addAttribute("recommendations", new ArrayList<Post>());
    }

    return "post/show";
  }

  @RequestMapping("/user/{userName}")
  public String show(@PathVariable String userName, Model model) throws Exception{
    if (StringUtils.isEmpty(userName)) {
      return "/";
    }

    User currentUser = userRepository.findByDisplayName(userName);
    if (currentUser == null) {
      return "/";
    }
    Set<Tag> tags = sessionService.retrieUserPreferredTags();
    if (tags.size() == 0){
      List<Tag> hotTopics = new ArrayList<>(sessionService.retrieveHotTopics());
      Collections.shuffle(hotTopics);
      tags.addAll(hotTopics.subList(0, 4));
    }
    List<Post> matchingPosts = new ArrayList<>();
    // Recommendations by Question's Tags
    matchingPosts.addAll(recommendUnansweredPostsByTags(new ArrayList<>(tags)));
    Set<Post> recommendedPosts = new HashSet<>(3);
    if (matchingPosts.size() > 3) {
      Collections.shuffle(matchingPosts);
      recommendedPosts.addAll(matchingPosts.subList(0, 3));
    } else {
      recommendedPosts.addAll(matchingPosts);
    }

    model.addAttribute("user", currentUser);
    model.addAttribute("preferredTags", tags);
    model.addAttribute("recommendations", recommendedPosts);

    return "user";
  }

  @RequestMapping(value = "/post/save", method = RequestMethod.POST)
  public String save(@RequestParam(value = "postId", required = true) long parentPostId,
                     @RequestParam(value = "body", required = true) String body, Model model) throws Exception {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    User currentUser = userRepository.findByDisplayName(username);
    long userId = currentUser.getId();

    Post post = new Post();
    post.setBody(body);
    post.setCreationDate(new Date());
    post.setOwnerUser(currentUser);
    post.setPostTypeId(2);

    Post parentPost = postRepository.findOne(parentPostId);
    parentPost.getAnswers().add(post);
    parentPost.setAnswerCount(parentPost.getAnswerCount() + 1);

    postRepository.save(parentPost);

    List<RecommendedItem> items;
    List<Post> matchingPosts = new ArrayList<>();

    // Recommendations by Question's Tags
    matchingPosts.addAll(recommendUnansweredPostsByParentsTags(parentPost));

    // Recommendations by User's History
//		matchingPosts.addAll(recommendPostsByUsers(currentUser));

    // Recommendations By Both
//		matchingPosts.addAll(recommendPostsByUsers(currentUser));
//		matchingPosts.addAll(recommendUnansweredPostsByParentsTags(parentPost));

    if (matchingPosts.size() < 3) {
      items = recommendItems(parentPost.getTags());

      for (RecommendedItem item : items) {
        List<Post> posts = postRepository.findUnansweredByTagId(item.getItemID(), new PageRequest(0, 3));

        matchingPosts.addAll(posts);
      }
    }

    Set<Post> recommendedPosts = new HashSet<>(3);

    if (matchingPosts.size() > 3) {
      Collections.shuffle(matchingPosts);
      recommendedPosts.addAll(matchingPosts.subList(0, 3));
    } else {
      recommendedPosts.addAll(matchingPosts);
    }

    model.addAttribute("post", parentPost);
    model.addAttribute("recommendations", recommendedPosts);
    sessionService.addToTodaysPreferredTags(parentPost.getTags());
    return "post/show";
  }

  private List<Post> recommendUnansweredPostsByParentsTags(Post parentPost) throws Exception {
    List<RecommendedItem> items = new ArrayList<>();

    for (Tag tag : parentPost.getTags()) {
      items.addAll(recommender.mostSimilarItems(tag.getId(), 10));
    }

    List<Post> matchingPosts = new ArrayList<>();

    for (RecommendedItem item : items) {
      List<Post> posts = postRepository.findUnansweredByTagId(item.getItemID(), new PageRequest(0, 3));

      matchingPosts.addAll(posts);
    }

    return matchingPosts;
  }

  private List<Post> recommendUnansweredPostsByTags(List<Tag> tags) throws Exception {
    List<RecommendedItem> items = new ArrayList<>();

    for (Tag tag : tags) {
      items.addAll(recommender.mostSimilarItems(tag.getId(), 10));
    }

    List<Post> matchingPosts = new ArrayList<>();

    for (RecommendedItem item : items) {
      List<Post> posts = postRepository.findUnansweredByTagId(item.getItemID(), new PageRequest(0, 3));

      matchingPosts.addAll(posts);
    }

    return matchingPosts;
  }

  private List<Post> recommendPostsByUsers(User user) throws Exception {
    List<RecommendedItem> items = recommender.recommend(user.getId(), 10);
    List<Post> matchingPosts = new ArrayList<>();

    for (RecommendedItem item : items) {
      List<Post> posts = postRepository.findUnansweredByTagId(item.getItemID(), new PageRequest(0, 3));

      matchingPosts.addAll(posts);
    }

    return matchingPosts;
  }

  private List<RecommendedItem> recommendItems(Set<Tag> tags) throws Exception {
    List<RecommendedItem> items = new ArrayList<>();

    for (Tag tag : tags) {
      items.addAll(recommender.mostSimilarItems(tag.getId(), 10));
    }

    return items;
  }
}
