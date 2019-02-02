package com.utcluj.recommender.service;

import com.utcluj.recommender.domain.User;
import com.utcluj.recommender.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;

public class CustomUserDetailsService implements UserDetailsService {

  @Value("${user.login-password}")
  private String password;
  private UserRepository userRepository;

  @Resource
  private SessionService sessionService;

  @Resource
  private AuthenticationProvider authenticationProvider;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByDisplayName(username);

    if (user != null) {
      UserDetails userDetails =
          org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder().username(username).password(password)
                                                            .roles("AUTHENTICATED").build();
      Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
      authenticationProvider.authenticate(authentication);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      sessionService.setAuthentication(authentication);
      return userDetails;
    } else {
      throw new UsernameNotFoundException("Unable to find " + username);
    }
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
