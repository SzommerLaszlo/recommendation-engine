package com.utcluj;

import com.utcluj.common.TagSimilarityDao;
import com.utcluj.common.TastePreferencesRepository;
import com.utcluj.common.model.TagSimilarity;
import com.utcluj.common.model.TastePreference;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    Iterable<TastePreference> allTastePreferences = tastePreferencesRepository.retrieveAll();
    Map<BigInteger, Set<BigInteger>> mapOfTagIdAndUsers = buildMap(allTastePreferences);
//    List<BigInteger> allUserIdsWhoHaveScoresOnTags =
//        tastePreferencesRepository.findAllUsersWhoHaveScoresOnTags(tagIdsWithExistingScores.get(1), tagIdsWithExistingScores.get(123));
//    List<BigDecimal> preferenceVectorTagA =
//        tastePreferencesRepository.retrievePreferenceVectorForTag(tagIdsWithExistingScores.get(1), allUserIdsWhoHaveScoresOnTags);
//    List<BigDecimal> preferenceVectorTagB =
//        tastePreferencesRepository.retrievePreferenceVectorForTag(tagIdsWithExistingScores.get(123), allUserIdsWhoHaveScoresOnTags);
//    double cosineSimilarity = Similarity.calculateCosineSimilarity(preferenceVectorTagA, preferenceVectorTagB);
//    System.out.println(cosineSimilarity);
//    calculateAndInsertCosineSimilarity(tagIdsWithExistingScores);
  }

  private Map<BigInteger, Set<BigInteger>> buildMap(Iterable<TastePreference> allTastePreferences) {
    Map<BigInteger, Set<BigInteger>> tagFilters = new HashMap<>();

    Iterator<TastePreference> iterator = allTastePreferences.iterator();
    while (iterator.hasNext()) {
      TastePreference tastePreference = iterator.next();
      Set<BigInteger> listOfUsers = tagFilters.get(tastePreference.getItemId());
      if (listOfUsers == null){
        Set<BigInteger> usersList = new HashSet();
        listOfUsers.add(BigInteger.valueOf(tastePreference.getUserId()));
        tagFilters.put(BigInteger.valueOf(tastePreference.getItemId()), listOfUsers);
      }
      else {
        listOfUsers.add(BigInteger.valueOf(tastePreference.getUserId()));
      }
    }
    return tagFilters;
  }

  private void calculateAndInsertCosineSimilarity(List<BigInteger> tagIdsWithExistingScores) {
    for (int i = 0; i < tagIdsWithExistingScores.size() - 1; i++) {
      for (int j = i + 1; j < tagIdsWithExistingScores.size(); j++) {
        List<BigInteger> allUserIdsWhoHaveScoresOnTags =
            tastePreferencesRepository.findAllUsersWhoHaveScoresOnTags(tagIdsWithExistingScores.get(i), tagIdsWithExistingScores.get(j));
        if (allUserIdsWhoHaveScoresOnTags.size() <= 0) {
          LOG.error("No result found for user id pair : " + tagIdsWithExistingScores.get(i) + "<>" + tagIdsWithExistingScores.get(j));
          continue;
        }
        List<BigDecimal> preferenceVectorTagA =
            tastePreferencesRepository.retrievePreferenceVectorForTag(tagIdsWithExistingScores.get(i), allUserIdsWhoHaveScoresOnTags);
        List<BigDecimal> preferenceVectorTagB =
            tastePreferencesRepository.retrievePreferenceVectorForTag(tagIdsWithExistingScores.get(j), allUserIdsWhoHaveScoresOnTags);
        float cosineSimilarity = Similarity.calculateCosineSimilarity(preferenceVectorTagA, preferenceVectorTagB);
        if (Float.isNaN(cosineSimilarity)){
          LOG.error("The calculations for tags " + tagIdsWithExistingScores.get(i) + " - " + tagIdsWithExistingScores.get(j) +
          " and their vectors: " + preferenceVectorTagA.toString() + "; " + preferenceVectorTagB.toString() + " is not a number !");
          continue;
        }

        TagSimilarity tagSimilarity = new TagSimilarity();
        tagSimilarity.setTagIdA(tagIdsWithExistingScores.get(i).intValue());
        tagSimilarity.setTagIdB(tagIdsWithExistingScores.get(j).intValue());
        tagSimilarity.setSimilarity(cosineSimilarity);

        TagSimilarity tagSimilarityMirror = new TagSimilarity();
        tagSimilarityMirror.setTagIdA(tagIdsWithExistingScores.get(j).intValue());
        tagSimilarityMirror.setTagIdB(tagIdsWithExistingScores.get(i).intValue());
        tagSimilarityMirror.setSimilarity(cosineSimilarity);

        tagSimilarityDao.createCosine(tagSimilarity);
        tagSimilarityDao.createCosine(tagSimilarityMirror);
      }
    }


  }
}
