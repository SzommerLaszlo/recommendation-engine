package com.utcluj.recommender.dataset.batch;

import com.utcluj.recommender.dataset.constants.ConfigVariables;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

  private static final Logger LOGGER = LogManager.getLogger(JobCompletionListener.class);

//  private final JdbcTemplate jdbcTemplate;

  private long startTime;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    super.beforeJob(jobExecution);

    startTime = Instant.now().getEpochSecond();
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    long endTime = Instant.now().getEpochSecond();
    long timeSeconds = endTime - startTime;
    LOGGER.info("Import job name : " + jobExecution.getJobInstance().getJobName());
    LOGGER.info("Status was : " + jobExecution.getStatus().toString());
    LOGGER.info("Number of steps executed : " + jobExecution.getStepExecutions().size());
    LOGGER.info("The duration in seconds was : " + timeSeconds);
    if (CollectionUtils.isNotEmpty(jobExecution.getAllFailureExceptions())) {
      LOGGER.error("There were exceptions during the execution : " + jobExecution.getAllFailureExceptions().toString());
    }
    LOGGER.info("Details of the executed steps :");
    LOGGER.info("######################################################################################");
    if (ConfigVariables.SHOW_DETAILED_INFO) {
      for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
        LOGGER.info("Step name : " + stepExecution.getStepName());
        LOGGER.info("Step status : " + stepExecution.getStatus().toString());
        LOGGER.info("Read count : " + stepExecution.getReadCount());
        LOGGER.info("Write count : " + stepExecution.getWriteCount());
        LOGGER.info("Commit count : " + stepExecution.getCommitCount());
        LOGGER.info("Rollback count : " + stepExecution.getRollbackCount());
        LOGGER.info("Read skip count : " + stepExecution.getReadSkipCount());
        LOGGER.info("Process skip count : " + stepExecution.getProcessSkipCount());
        LOGGER.info("Write skip count : " + stepExecution.getWriteSkipCount());
        LOGGER.info("######################################################################################");
      }
    }
  }

//    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
//      LOGGER.info("!!! JOB FINISHED! Time to verify the results");

//      List<Person> results = jdbcTemplate.query("SELECT first_name, last_name FROM people", new RowMapper<Person>() {
//        @Override
//        public Person mapRow(ResultSet rs, int row) throws SQLException {
//          return new Person(rs.getString(1), rs.getString(2));
//        }
//      });
//
//      for (Person person : results) {
//        log.info("Found <" + person + "> in the database.");
//  }

//  @Autowired
//  public JobCompletionListener(JdbcTemplate jdbcTemplate) {
//    this.jdbcTemplate = jdbcTemplate;
//  }
}
