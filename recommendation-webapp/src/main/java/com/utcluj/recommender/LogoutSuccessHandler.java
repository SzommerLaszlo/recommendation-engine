package com.utcluj.recommender;

import com.utcluj.recommender.service.SessionService;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

  @Resource
  private SessionService sessionService;

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException,
                                                                                                                              ServletException {
    sessionService.resetValues();
    super.onLogoutSuccess(request, response, authentication);
  }
}
