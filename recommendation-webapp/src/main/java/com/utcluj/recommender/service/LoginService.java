package com.utcluj.recommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LoginService {

  @Resource
  protected AuthenticationProvider authenticationProvider;

  @Autowired
  protected SessionService sessionService;

  public Authentication verifyAndSetAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Authentication sessionAuth = sessionService.getAuthentication();
    if (sessionAuth != null && authentication != sessionAuth) {
      authenticationProvider.authenticate(sessionAuth);
      SecurityContextHolder.getContext().setAuthentication(sessionAuth);
    }
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public String retrieveLoggedInUsersName() {
    Authentication authentication = verifyAndSetAuthentication();
    return authentication.getName();
  }
}
