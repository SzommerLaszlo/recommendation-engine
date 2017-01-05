package com.utcluj.recommender.dataset.services;

/**
 * Service to provide tag related operations.
 */
public interface TagService {

  /**
   * Method used to retrieve the tag's id from the database or insert it if does not exist.
   * @param tag The tag to search for.
   * @return The tag id.
   */
  long getTagId(final String tag);
}
