package com.utcluj.common.model;

import java.io.Serializable;
import java.math.BigInteger;

public class TastePreferenceId implements Serializable {

  private BigInteger userId;
  private BigInteger itemId;

  public TastePreferenceId() {
  }

  public TastePreferenceId(BigInteger userId, BigInteger itemId) {
    this.userId = userId;
    this.itemId = itemId;
  }
}