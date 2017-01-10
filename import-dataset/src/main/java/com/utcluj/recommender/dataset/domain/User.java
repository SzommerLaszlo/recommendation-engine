package com.utcluj.recommender.dataset.domain;

import java.util.Date;

public class User extends Entity {
  private int reputation;
  private Date creationDate;
  private String displayName;
  private Date lastAccessDate;
  private String location;
  private String about;
  private int views;
  private int upVotes;
  private int downVotes;

  public int getReputation() {
    return reputation;
  }

  public void setReputation(int reputation) {
    this.reputation = reputation;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Date getLastAccessDate() {
    return lastAccessDate;
  }

  public void setLastAccessDate(Date lastAccessDate) {
    this.lastAccessDate = lastAccessDate;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public int getViews() {
    return views;
  }

  public void setViews(int views) {
    this.views = views;
  }

  public int getUpVotes() {
    return upVotes;
  }

  public void setUpVotes(int upVotes) {
    this.upVotes = upVotes;
  }

  public int getDownVotes() {
    return downVotes;
  }

  public void setDownVotes(int downVotes) {
    this.downVotes = downVotes;
  }
}