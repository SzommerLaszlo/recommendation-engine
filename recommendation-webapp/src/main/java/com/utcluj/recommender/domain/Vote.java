package com.utcluj.recommender.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "VOTES")
public class Vote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Version
  private long version;

  @ManyToOne
  @JoinColumn(name = "POST_ID")
  private Post post;

  @Column(name = "VOTE_TYPE")
  private int voteType;

  @Column(name = "CREATION_DATE")
  private Date creationDate;

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

  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public int getVoteType() {
    return voteType;
  }

  public void setVoteType(int voteType) {
    this.voteType = voteType;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
}
