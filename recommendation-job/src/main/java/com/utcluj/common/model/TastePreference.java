package com.utcluj.common.model;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(TastePreferenceId.class)
public class TastePreference {

  @Id
  @Column(name = "user_id")
  private BigInteger userId;
  @Id
  @Column(name = "item_id")
  private BigInteger itemId;

  @Column(name = "preference")
  private float preference;

  public BigInteger getUserId() {
    return userId;
  }

  public void setUserId(BigInteger userId) {
    this.userId = userId;
  }

  public BigInteger getItemId() {
    return itemId;
  }

  public void setItemId(BigInteger itemId) {
    this.itemId = itemId;
  }

  public float getPreference() {
    return preference;
  }

  public void setPreference(float preference) {
    this.preference = preference;
  }
}
