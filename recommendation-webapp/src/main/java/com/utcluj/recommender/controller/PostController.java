package com.utcluj.recommender.controller;

import com.utcluj.recommender.domain.PageWrapper;
import com.utcluj.recommender.domain.Post;
import com.utcluj.recommender.domain.Tag;
import com.utcluj.recommender.domain.User;
import com.utcluj.recommender.repositories.PostRepository;
import com.utcluj.recommender.repositories.UserRepository;
import com.utcluj.recommender.service.LoginService;
import com.utcluj.recommender.service.RecommendationService;
import com.utcluj.recommender.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
public class PostController {

  @Autowired
  protected PostRepository postRepository;

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected SessionService sessionService;

  @Autowired
  protected RecommendationService recommendationService;

  @Autowired
  protected LoginService loginService;

  @RequestMapping("/")
  public String homepage(Pageable pageable, Model model) {
    Page<Post> curPage = postRepository.findAllByPostTypeIdOrderByCreationDateDesc(1, pageable);
    String usersName = loginService.retrieveLoggedInUsersName();
    if (!usersName.toLowerCase().contains("anonymous")) {
      Set<Tag> hotTopics = sessionService.retrieveHotTopics();
      model.addAttribute("hotTopics", hotTopics);
    }
    PageWrapper<Post> page = new PageWrapper<>(curPage, "/");
    model.addAttribute("page", page);
    return "post/index";
  }

  @RequestMapping("/post/{id}")
  public String showPostDetails(@PathVariable long id, Model model) {
    loginService.verifyAndSetAuthentication();
    Post curPost = postRepository.findById(id).get();
    curPost.setViewCount(curPost.getViewCount() + 1);
    postRepository.save(curPost);
    model.addAttribute("post", curPost);
    if (!model.containsAttribute("recommendations")) {
      model.addAttribute("recommendations", new ArrayList<Post>());
    }

    return "post/show";
  }

  @RequestMapping(value = "/post/save", method = RequestMethod.POST)
  public String savePostResponse(@RequestParam(value = "postId", required = true) long parentPostId,
                                 @RequestParam(value = "body", required = true) String body, Model model) throws Exception {
    String username = loginService.retrieveLoggedInUsersName();
    User currentUser = userRepository.findByDisplayName(username);

    Post post = new Post();
    post.setBody(body);
    post.setCreationDate(new Date());
    post.setOwnerUser(currentUser);
    post.setPostTypeId(2);

    Post parentPost = postRepository.findById(parentPostId).get();
    parentPost.getAnswers().add(post);
    parentPost.setAnswerCount(parentPost.getAnswerCount() + 1);

    postRepository.save(parentPost);
    // Recommendations by Question's Tags
    List<Post> matchingPosts = recommendationService.recommendUnansweredPostsByParentsTags(parentPost);
    // Recommendations by User's History
//		matchingPosts.addAll(recommendPostsByUsers(currentUser));

    if (matchingPosts.size() < 3) {
      List<Post> posts = recommendationService.recommendUnansweredPostsForTags(parentPost.getTags());
      matchingPosts.addAll(posts);
    }

    Set<Post> recommendedPosts = recommendationService.shuffleAndCut(matchingPosts, 3);

    model.addAttribute("post", parentPost);
    model.addAttribute("recommendations", recommendedPosts);
    sessionService.addToTodaysPreferredTags(parentPost.getTags());
    return "post/show";
  }
}
