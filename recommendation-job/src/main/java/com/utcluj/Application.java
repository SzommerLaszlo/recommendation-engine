package com.utcluj;

import com.utcluj.common.algorithms.CosineSimilarity;
import com.utcluj.common.algorithms.LogLikelihoodSimilarity;
import com.utcluj.common.repository.TagSimilarityDao;
import com.utcluj.common.repository.TastePreferencesRepository;
import com.utcluj.common.model.TagSimilarity;
import com.utcluj.common.model.TastePreference;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

import javax.sql.DataSource;

import static com.utcluj.common.configurations.ConfigVariables.*;

@SpringBootApplication
public class Application implements CommandLineRunner {

  public static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
  private static Logger LOG = LoggerFactory.getLogger(Application.class);

  @Lazy
  @Autowired
  TagSimilarityDao tagSimilarityDao;

  @Lazy
  @Autowired
  TastePreferencesRepository tastePreferencesRepository;

  @Bean(name = "dataSource")
  @ConditionalOnMissingBean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(DRIVER_CLASS_NAME);
    dataSource.setUrl(DATABASE_LOCATION);
    dataSource.setUsername(DATABASE_USERNAME);
    dataSource.setPassword(DATABASE_PASSWORD);
    return dataSource;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... strings) {
    List<BigInteger> tagIdsWithExistingScores = tastePreferencesRepository.retrieveTagIdsWithExistingScores();
    List<TastePreference> allTastePreferences = tastePreferencesRepository.retrieveAll();
    Map<BigInteger, Set<BigInteger>> tagIdAndUsersWhoScoredIt = buildTagUsersMapping(allTastePreferences);
    LOG.info("Starting to calculate and insert the cosine similarity: ");
    LOG.info("######################################################################################");
    long startTime = Instant.now().getEpochSecond();
    calculateAndInsertCosineSimilarity(allTastePreferences, tagIdAndUsersWhoScoredIt, tagIdsWithExistingScores);
    long endTime = Instant.now().getEpochSecond();
    LOG.info("Duration for calculating cosine similarity was :" + (endTime - startTime) + " seconds");
    LOG.info("######################################################################################");

    LOG.info("Starting to calculate the log likelihood similarity :");
    LOG.info("######################################################################################");
    startTime = Instant.now().getEpochSecond();
    calculateAndInsertLogLikelihoodSimilarity(tagIdAndUsersWhoScoredIt, tagIdsWithExistingScores);
    endTime = Instant.now().getEpochSecond();
    LOG.info("Duration for calculating log likelihood similarity was:" + (endTime - startTime) + " seconds");
    LOG.info("######################################################################################");
  }

  private void calculateAndInsertCosineSimilarity(List<TastePreference> allTastePreferences,
                                                  Map<BigInteger, Set<BigInteger>> tagIdAndUsersWhoScoredIt,
                                                  List<BigInteger> listOfTagIds) {
    List<TagSimilarity> tagSimilarities = new ArrayList<>();
    LOG.info("Total number of iterations to compare all tags : " + listOfTagIds.size());
    for (int indexOfTagIdA = 0; indexOfTagIdA < listOfTagIds.size() - 1; indexOfTagIdA++) {
//      LOG.info("This is the n: " + indexOfTagIdA + " iteration from a total of : " + listOfTagIds.size());
      for (int indexOfTagIdB = indexOfTagIdA + 1; indexOfTagIdB < listOfTagIds.size(); indexOfTagIdB++) {
        Collection<BigInteger> intersection =
            CollectionUtils.intersection(tagIdAndUsersWhoScoredIt.get(listOfTagIds.get(indexOfTagIdA)),
                                         tagIdAndUsersWhoScoredIt.get(listOfTagIds.get(indexOfTagIdB)));
        if (intersection.size() <= 0) {
          LOG.debug("No result found for user id pair : " + listOfTagIds.get(indexOfTagIdA) + "<>" + listOfTagIds.get(indexOfTagIdB));
          continue;
        }

        List<BigDecimal> preferenceVectorTagA = new ArrayList<>();
        List<BigDecimal> preferenceVectorTagB = new ArrayList<>();
        retrievePreferenceVectors(allTastePreferences, listOfTagIds, indexOfTagIdA, indexOfTagIdB, intersection, preferenceVectorTagA,
                                  preferenceVectorTagB);

        float cosineSimilarity = CosineSimilarity.calculateCosineSimilarity(preferenceVectorTagA, preferenceVectorTagB);
        if (Float.isNaN(cosineSimilarity)) {
          LOG.debug("The calculations for tags " + listOfTagIds.get(indexOfTagIdA) + " - " + listOfTagIds.get(indexOfTagIdB) +
                    " and their vectors: " + preferenceVectorTagA.toString() + "; " + preferenceVectorTagB.toString() + " is not a number !");
          continue;
        }

        if (cosineSimilarity < 0) {
          continue;
        }

        //normalize the final value
        if (cosineSimilarity >= 1) {
          cosineSimilarity = (float) 0.99;
        }
        TagSimilarity tagSimilarity = new TagSimilarity();
        tagSimilarity.setTagIdA(listOfTagIds.get(indexOfTagIdA).intValue());
        tagSimilarity.setTagIdB(listOfTagIds.get(indexOfTagIdB).intValue());
        tagSimilarity.setSimilarity(cosineSimilarity);

        TagSimilarity tagSimilarityMirror = new TagSimilarity();
        tagSimilarityMirror.setTagIdA(listOfTagIds.get(indexOfTagIdB).intValue());
        tagSimilarityMirror.setTagIdB(listOfTagIds.get(indexOfTagIdA).intValue());
        tagSimilarityMirror.setSimilarity(cosineSimilarity);
        tagSimilarities.add(tagSimilarity);
        tagSimilarities.add(tagSimilarityMirror);

        if (tagSimilarities.size() > BATCH_SIZE) {
          tagSimilarityDao.createBatchCosine(tagSimilarities);
          tagSimilarities.clear();
        }
      }
    }
    tagSimilarityDao.createBatchCosine(tagSimilarities);
  }

  private void calculateAndInsertLogLikelihoodSimilarity(Map<BigInteger, Set<BigInteger>> tagIdAndUsersWhoScoredIt, List<BigInteger> listOfTagIds) {
    List<TagSimilarity> tagSimilarities = new ArrayList<>();
    LOG.info("Total number of iterations to compare all tags : " + listOfTagIds.size());
    for (int indexOfTagIdA = 0; indexOfTagIdA < listOfTagIds.size() - 1; indexOfTagIdA++) {
//      LOG.info("This is the n: " + indexOfTagIdA + " iteration from a total of : " + listOfTagIds.size());
      for (int indexOfTagIdB = indexOfTagIdA + 1; indexOfTagIdB < listOfTagIds.size(); indexOfTagIdB++) {
        int nrOfTimesTheEventsOccurredTogether =
            CollectionUtils.intersection(tagIdAndUsersWhoScoredIt.get(listOfTagIds.get(indexOfTagIdA)),
                                         tagIdAndUsersWhoScoredIt.get(listOfTagIds.get(indexOfTagIdB))).size();
        int nrOfTimesOnlyTheFirst = tagIdAndUsersWhoScoredIt.get(listOfTagIds.get(indexOfTagIdA)).size() - nrOfTimesTheEventsOccurredTogether;
        int nrOfTimesOnlyTheSecond = tagIdAndUsersWhoScoredIt.get(listOfTagIds.get(indexOfTagIdB)).size() - nrOfTimesTheEventsOccurredTogether;
        int nrOfTimesSomethingElseOccurred =
            tagIdAndUsersWhoScoredIt.size() - nrOfTimesOnlyTheFirst - nrOfTimesOnlyTheSecond + nrOfTimesTheEventsOccurredTogether;

        double logLikelihoodRatio =
            LogLikelihoodSimilarity.logLikelihoodRatio(nrOfTimesTheEventsOccurredTogether, nrOfTimesOnlyTheSecond, nrOfTimesOnlyTheFirst,
                                                       nrOfTimesSomethingElseOccurred);
        double logLikelihoodSimilarity = 1.0 - (1.0 / logLikelihoodRatio);
        if (logLikelihoodSimilarity < 0) {
          continue;
        }
        TagSimilarity tagSimilarity = new TagSimilarity();
        tagSimilarity.setTagIdA(listOfTagIds.get(indexOfTagIdA).intValue());
        tagSimilarity.setTagIdB(listOfTagIds.get(indexOfTagIdB).intValue());
        tagSimilarity.setSimilarity((float) logLikelihoodSimilarity);

        TagSimilarity tagSimilarityMirror = new TagSimilarity();
        tagSimilarityMirror.setTagIdA(listOfTagIds.get(indexOfTagIdB).intValue());
        tagSimilarityMirror.setTagIdB(listOfTagIds.get(indexOfTagIdA).intValue());
        tagSimilarityMirror.setSimilarity((float) logLikelihoodSimilarity);
        tagSimilarities.add(tagSimilarity);
        tagSimilarities.add(tagSimilarityMirror);

        if (tagSimilarities.size() > BATCH_SIZE) {
          tagSimilarityDao.createBatchLogLikelihood(tagSimilarities);
          tagSimilarities.clear();
        }
      }
    }
    tagSimilarityDao.createBatchLogLikelihood(tagSimilarities);
  }

  /**
   * Builds a map which contains for every tag_id the list of users who have scores for it.
   */
  private Map<BigInteger, Set<BigInteger>> buildTagUsersMapping(List<TastePreference> allTastePreferences) {
    Map<BigInteger, Set<BigInteger>> tagUsersMapping = new HashMap<>();
    for (TastePreference tastePreference : allTastePreferences) {
      Set<BigInteger> listOfUsers = tagUsersMapping.get(tastePreference.getItemId());
      if (listOfUsers == null) {
        Set<BigInteger> usersList = new HashSet();
        usersList.add(tastePreference.getUserId());
        tagUsersMapping.put(tastePreference.getItemId(), usersList);
      } else {
        listOfUsers.add(tastePreference.getUserId());
      }
    }
    return tagUsersMapping;
  }

  private void retrievePreferenceVectors(List<TastePreference> allTastePreferences, List<BigInteger> listOfTagIds, int indexOfTagIdA,
                                         int indexOfTagIdB, Collection<BigInteger> intersection, List<BigDecimal> preferenceVectorTagA,
                                         List<BigDecimal> preferenceVectorTagB) {
    for (BigInteger userId : intersection) {
      boolean foundA = false;
      boolean foundB = false;
      for (TastePreference tastePreference : allTastePreferences) {
        if (foundA == false && tastePreference.getItemId().intValue() == listOfTagIds.get(indexOfTagIdA).intValue()
            && tastePreference.getUserId().intValue() == userId.intValue()) {
          preferenceVectorTagA.add(BigDecimal.valueOf((long) tastePreference.getPreference()));
          foundA = true;
        }
        if (foundB == false && tastePreference.getItemId().intValue() == listOfTagIds.get(indexOfTagIdB).intValue()
            && tastePreference.getUserId().intValue() == userId.intValue()) {
          preferenceVectorTagB.add(BigDecimal.valueOf((long) tastePreference.getPreference()));
          foundB = true;
        }
        if (foundA && foundB) {
          break;
        }
      }
    }
  }
}
