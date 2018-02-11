package com.utcluj.recommender.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
@Cacheable
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Version
  private long version;

  @Column(name = "REPUTATION")
  private int reputation;

  @Column(name = "LOCATION")
  private String location;

  @Column(name = "ABOUT")
  private String about;

  @Column(name = "VIEWS")
  private int views;

  @Column(name = "CREATION_DATE")
  private Date creationDate;

  @Column(name = "DISPLAY_NAME")
  private String displayName;

  @Column(name = "LAST_ACCESS_DATE")
  private Date lastAccessDate;

  @Column(name = "UP_VOTES")
  private int upVotes;

  @Column(name = "DOWN_VOTES")
  private int downVotes;

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