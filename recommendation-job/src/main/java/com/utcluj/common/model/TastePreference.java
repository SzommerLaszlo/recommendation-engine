package com.utcluj.common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(TastePreference.TastePreferenceId.class)
public class TastePreference implements Serializable {
  @Id
  @Column(name = "user_id")
  private int userId;
  @Id
  @Column(name = "item_id")
  private int itemId;
  @Column(name = "preference")
  private float preference;

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public float getPreference() {
    return preference;
  }

  public void setPreference(float preference) {
    this.preference = preference;
  }

  public class TastePreferenceId implements Serializable {
    private int userId;
    private int itemId;

    public TastePreferenceId(int userId, int itemId) {
      this.userId = userId;
      this.itemId = itemId;
    }
  }
}
