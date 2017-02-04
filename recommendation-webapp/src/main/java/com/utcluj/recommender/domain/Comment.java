package com.utcluj.recommender.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "COMMENTS")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Version
  private long version;

  @ManyToOne
  @JoinColumn(name = "POST_ID")
  private Post post;

  @Column(name = "VALUE")
  private String value;

  @Column(name = "CREATION_DATE")
  private Date creationDate;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  @Column(name = "SCORE")
  private int score;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}