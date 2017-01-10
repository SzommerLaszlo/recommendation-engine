package com.utcluj.recommender.dataset.domain;

import java.util.Date;

public class Vote extends Entity {
  private long postId;
  private int voteType;
  private Date creationDate;

  public long getPostId() {
    return postId;
  }

  public void setPostId(long postId) {
    this.postId = postId;
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