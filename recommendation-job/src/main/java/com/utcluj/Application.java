package com.utcluj;

import com.utcluj.common.TagSimilarityDao;
import com.utcluj.common.TastePreferencesRepository;
import com.utcluj.common.model.TagSimilarity;
import com.utcluj.common.model.TastePreference;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties

public class Application implements CommandLineRunner {

  private static Logger LOG = LoggerFactory.getLogger(Application.class);

  @Autowired
  TagSimilarityDao tagSimilarityDao;

  @Autowired
  TastePreferencesRepository tastePreferencesRepository;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... strings) {
    List<BigInteger> tagIdsWithExistingScores = tastePreferencesRepository.retrieveTagIdsWithExistingScores();

    List<TastePreference> allTastePreferences = tastePreferencesRepository.retrieveAll();
    Map<BigInteger, Set<BigInteger>> tagIdAndUsersWhoScoredIt = buildTagUsersMapping(allTastePreferences);
    calculateAndInsertCosineSimilarity(allTastePreferences, tagIdAndUsersWhoScoredIt, tagIdsWithExistingScores);
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

  private void calculateAndInsertCosineSimilarity(List<TastePreference> allTastePreferences,
                                                  Map<BigInteger, Set<BigInteger>> tagIdAndUsersWithScores, List<BigInteger> listOfTagIds) {
    List<TagSimilarity> tagSimilarities = new ArrayList<>();
    for (int indexOfTagIdA = 0; indexOfTagIdA < listOfTagIds.size() - 1; indexOfTagIdA++) {
      LOG.info("This is the n: " + indexOfTagIdA + " iteration");
      for (int indexOfTagIdB = indexOfTagIdA + 1; indexOfTagIdB < listOfTagIds.size(); indexOfTagIdB++) {
        Collection<BigInteger> intersection =
            CollectionUtils.intersection(tagIdAndUsersWithScores.get(listOfTagIds.get(indexOfTagIdA)),
                                         tagIdAndUsersWithScores.get(listOfTagIds.get(indexOfTagIdB)));
        if (intersection.size() <= 0) {
          LOG.debug("No result found for user id pair : " + listOfTagIds.get(indexOfTagIdA) + "<>" + listOfTagIds.get(indexOfTagIdB));
          continue;
        }

        List<BigDecimal> preferenceVectorTagA = new ArrayList<>();
        List<BigDecimal> preferenceVectorTagB = new ArrayList<>();
        retrievePreferenceVectors(allTastePreferences, listOfTagIds, indexOfTagIdA, indexOfTagIdB, intersection, preferenceVectorTagA,
                                  preferenceVectorTagB);

        float cosineSimilarity = Similarity.calculateCosineSimilarity(preferenceVectorTagA, preferenceVectorTagB);
        if (Float.isNaN(cosineSimilarity)) {
          LOG.debug("The calculations for tags " + listOfTagIds.get(indexOfTagIdA) + " - " + listOfTagIds.get(indexOfTagIdB) +
                    " and their vectors: " + preferenceVectorTagA.toString() + "; " + preferenceVectorTagB.toString() + " is not a number !");
          continue;
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

        if (tagSimilarities.size() > 250) {
          tagSimilarityDao.createBatchCosine(tagSimilarities);
          LOG.info("A 250 batch size was sent, clearing the results and continue with the calculations.");
          tagSimilarities.clear();
        }
      }
    }
    tagSimilarityDao.createBatchCosine(tagSimilarities);
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
