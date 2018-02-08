package com.utcluj.common.model;

public class TagSimilarity {

  private int tagIdA;
  private int tagIdB;
  private float similarity;

  public int getTagIdA() {
    return tagIdA;
  }

  public void setTagIdA(int tagIdA) {
    this.tagIdA = tagIdA;
  }

  public int getTagIdB() {
    return tagIdB;
  }

  public void setTagIdB(int tagIdB) {
    this.tagIdB = tagIdB;
  }

  public float getSimilarity() {
    return similarity;
  }

  public void setSimilarity(float similarity) {
    this.similarity = similarity;
  }
}
