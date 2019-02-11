package com.utcluj.recommender.controller;


import com.utcluj.recommender.domain.Post;
import com.utcluj.recommender.domain.Tag;
import com.utcluj.recommender.domain.User;
import com.utcluj.recommender.repositories.UserRepository;
import com.utcluj.recommender.service.LoginService;
import com.utcluj.recommender.service.RecommendationService;
import com.utcluj.recommender.service.SessionService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected SessionService sessionService;

  @Autowired
  protected RecommendationService recommendationService;

  @Autowired
  protected LoginService loginService;

  @RequestMapping("/user/{userName}")
  public String show(@PathVariable String userName, Model model) throws Exception {
    loginService.verifyAndSetAuthentication();
    if (StringUtils.isEmpty(userName)) {
      return "/";
    }
    User currentUser = userRepository.findByDisplayName(userName);
    if (currentUser == null) {
      return "/";
    }
    Set<Tag> tags = sessionService.retrieUserPreferredTags();
    if (tags.size() == 0) {
      List<Tag> hotTopics = new ArrayList<>(sessionService.retrieveHotTopics());
      Collections.shuffle(hotTopics);
      tags.addAll(hotTopics.subList(0, 4));
    }

    // Recommendations by Question's Tags
    List<Post> matchingPosts = recommendationService.recommendUnansweredPostsForTags(tags);
    Set<Post> recommendedPosts = recommendationService.shuffleAndCut(matchingPosts, 3);

    model.addAttribute("user", currentUser);
    model.addAttribute("preferredTags", tags);
    model.addAttribute("recommendations", recommendedPosts);

    return "user";
  }
}
