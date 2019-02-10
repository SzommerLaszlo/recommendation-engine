package com.utcluj.common.configurations;

/**
 * Class used to specify the used variables during the similarity calculation.
 */
public interface ConfigVariables {

  /**
   * Variable to fine tune the batch size to write to database.
   */
  int BATCH_SIZE = 10000;
  /**
   * Specifies the location of the database
   */
  String DATABASE_LOCATION = "jdbc:mysql://localhost:3306/recommendation";
  /**
   * Specifies the username for the database
   */
  String DATABASE_USERNAME = "root";
  /**
   * Specifies the password for the database
   */
  String DATABASE_PASSWORD = "root";
}
