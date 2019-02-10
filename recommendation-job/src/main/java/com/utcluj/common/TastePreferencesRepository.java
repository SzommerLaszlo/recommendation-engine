package com.utcluj.common;

import com.utcluj.common.model.TastePreference;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface TastePreferencesRepository extends CrudRepository<TastePreference, Long> {

  String TAGS_IDS_WITH_EXISTING_SCORES = "SELECT distinct item_id FROM users_preferences order by item_id asc";
  @Query(value = TAGS_IDS_WITH_EXISTING_SCORES, nativeQuery = true)
  List<BigInteger> retrieveTagIdsWithExistingScores();

  String RETRIEVE_ALL_users_preferences = "SELECT * FROM users_preferences order by item_id asc";
  @Query(value = RETRIEVE_ALL_users_preferences, nativeQuery = true)
  List<TastePreference> retrieveAll();

  /**
  String FIND_USERS_WHO_HAVE_SCORES_FOR_TAGS =
      "SELECT A.user_id FROM users_preferences A"
      + " inner join (SELECT B.user_id FROM users_preferences B where B.item_id = :tagIdB) as aliasB on A.user_id = aliasB.user_id"
      + " where A.item_id = :tagIdA order by A.user_id asc";


  @Query(value = FIND_USERS_WHO_HAVE_SCORES_FOR_TAGS, nativeQuery = true)
  List<BigInteger> findAllUsersWhoHaveScoresOnTags(@Param(value = "tagIdA") BigInteger tagIdA, @Param(value = "tagIdB") BigInteger tagIdB);
  String PREFERENCE_VECTOR_FOR_TAG = "SELECT preference FROM users_preferences where item_id = :tagId and user_id in :usersList order by user_id asc";


  @Query(value = PREFERENCE_VECTOR_FOR_TAG, nativeQuery = true)
  List<BigDecimal> retrievePreferenceVectorForTag(@Param(value = "tagId") BigInteger tagId,
                                                  @Param(value = "usersList") List<BigInteger> usersWhoHaveScore);
  **/
}
