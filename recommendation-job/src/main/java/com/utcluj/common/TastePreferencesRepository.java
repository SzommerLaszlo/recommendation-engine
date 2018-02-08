package com.utcluj.common;

import com.utcluj.common.model.TastePreference;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface TastePreferencesRepository extends CrudRepository<TastePreference, Long> {

  String FIND_USERS_WHO_HAVE_SCORES_FOR_TAGS =
      "Select A.user_id from taste_preferences A"
      + " inner join (select B.user_id from taste_preferences B where B.item_id = :tagIdB) as aliasB on A.user_id = aliasB.user_id"
      + " where A.item_id = :tagIdA order by A.user_id asc";

  @Query(value = FIND_USERS_WHO_HAVE_SCORES_FOR_TAGS, nativeQuery = true)
  List<BigInteger> findAllUsersWhoHaveScoresOnTags(@Param(value = "tagIdA") BigInteger tagIdA, @Param(value = "tagIdB") BigInteger tagIdB);

  String PREFERENCE_VECTOR_FOR_TAG = "SELECT preference FROM taste_preferences where item_id = :tagId and user_id in :usersList order by user_id asc";

  @Query(value = PREFERENCE_VECTOR_FOR_TAG, nativeQuery = true)
  List<BigDecimal> retrievePreferenceVectorForTag(@Param(value = "tagId") BigInteger tagId,
                                                  @Param(value = "usersList") List<BigInteger> usersWhoHaveScore);

  String TAGS_IDS_WITH_EXISTING_SCORES = "SELECT distinct item_id FROM taste_preferences order by item_id asc";

  @Query(value = TAGS_IDS_WITH_EXISTING_SCORES, nativeQuery = true)
  List<BigInteger> retrieveTagIdsWithExistingScores();

  @Query(value = "select * from taste_preferences", nativeQuery = true)
  List<TastePreference> retrieveAll();

}
