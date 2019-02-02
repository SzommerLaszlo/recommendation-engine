package com.utcluj.recommender.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.unbescape.html.HtmlEscape;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

  // Login form
  @RequestMapping("/login.html")
  public ModelAndView login() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("login");
    return modelAndView;
  }

  /** Error page. */
  @RequestMapping("/error.html")
  public String error(HttpServletRequest request, Model model) {
    model.addAttribute("errorCode", "Error " + request.getAttribute("javax.servlet.error.status_code"));
    Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
    StringBuilder errorMessage = new StringBuilder();
    errorMessage.append("<ul>");
    while (throwable != null) {
      errorMessage.append("<li>").append(HtmlEscape.escapeHtml5(throwable.getMessage())).append("</li>");
      throwable = throwable.getCause();
    }
    errorMessage.append("</ul>");
    model.addAttribute("errorMessage", errorMessage.toString());
    return "error";
  }

  /** Error page. */
  @RequestMapping("/403.html")
  public String forbidden() {
    return "403";
  }
}