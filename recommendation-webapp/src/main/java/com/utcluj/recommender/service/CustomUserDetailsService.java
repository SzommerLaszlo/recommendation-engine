package com.utcluj.recommender.service;

import com.utcluj.recommender.domain.User;
import com.utcluj.recommender.domain.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class CustomUserDetailsService implements UserDetailsService {

  @Value("${user.login-password}")
  private String password;
  private UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByDisplayName(username);

    if (user != null) {
      return new org.springframework.security.core.userdetails.User(username, password, Collections
          .singletonList(new SimpleGrantedAuthority("ROLE_AUTHENTICATED")));
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
