package com.utcluj.recommender.dataset.constants;

/**
 * Class used to specify the used variables in the import job.
 */
public interface ConfigVariables {

  /**
   * The number of users and their connected information (ex. posts, comments) to import.
   */
  int NUMBER_OF_USERS_TO_IMPORT = 1000; //1000;
  /**
   * The path to the dataset .xml files.
   */
  String PATH_TO_DATASET = "d:\\licenta\\recommendation-engine\\dataset\\";
  /**
   * Allow for spring batch to re-import the users if they were already imported.
   * Accepted values are true or false
   */
  boolean ALLOW_REIMPORT_OF_USERS = true;
  /**
   * Set this to true if you want detailed information about the finished job's executed steps.
   */
  boolean SHOW_DETAILED_INFO = true;
}
