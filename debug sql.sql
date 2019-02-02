SELECT * FROM recommendation.taste_preferences;

SELECT * FROM recommendation.taste_preferences where item_id in (160,266,326);

SELECT * FROM recommendation.taste_preferences where user_id=449;

SELECT * FROM recommendation.taste_preferences where tag_to_tag_similarity=449;

SELECT * FROM tags where tags.ID=160;

SELECT * FROM tags where tags.ID=160 or id=326 or id=266;

Select users.DISPLAY_NAME from users where users.id="50";
Select tags.TAG from tags where tags.id="1" or tags.id="127";



-- documentation:
 -- http://grepcode.com/file/repo1.maven.org/maven2/org.apache.mahout/mahout-math/0.6/org/apache/mahout/math/stats/LogLikelihood.java#LogLikelihood.xLogX%28long%29
 -- http://www.statisticshowto.com/probability-and-statistics/correlation-coefficient-formula/
 -- https://blog.guillaumeagis.eu/recommendation-algorithms-with-apache-mahout/
 -- http://times.cs.uiuc.edu/course/410/note/mle.pdf
 
-- Cosine similarity
-- Select all of the users who have score for tags with id 160, 266
Select A.user_id from taste_preferences A 
	inner join (select B.user_id from taste_preferences B where B.item_id = 160) as aliasB on A.user_id = aliasB.user_id where A.item_id = 266 order by A.user_id asc;

-- Select the scores of the list of users for a specific tag.
SELECT preference FROM taste_preferences where item_id = 160 and user_id in (4, 42, 67, 132, 163, 235, 254, 275, 289, 334, 449, 455, 458, 629, 721, 742, 861, 880, 935, 1059, 1067, 1103, 1158, 1241) order by user_id asc;

insert into tag_to_tag_similarity_cosine (tag_id_a,tag_id_b,similarity) values (1, 2, 0.92) on duplicate key update similarity=0.92;

-- log_likelihood
-- public static double  [More ...] logLikelihoodRatio(long k11, long k12, long k21, long k22) {
-- Parameters:
--    k11 The number of times the two events occurred together
--    k12 The number of times the second event occurred WITHOUT the first event
--    k21 The number of times the first event occurred WITHOUT the second event
--    k22 The number of times something else occurred (i.e. was neither of these events
-- first event= tagid160
-- second event = tagid266
-- calc for k11
-- Select all of the users who have scores for tags with id 160,266
Select A.user_id from taste_preferences A 
	inner join (select B.user_id from taste_preferences B where B.item_id = 160) as aliasB on A.user_id = aliasB.user_id where A.item_id = 266 order by A.user_id asc;
-- =24


-- calc for k12
-- the users who scores the second event withouth the first
-- = 30-24=6

-- calc for k21
-- the users who scores the first event withouth the second
-- = 80-24=56

-- k22 The number of times something else occurred (i.e. was neither of these events)
-- = 450 - 80 - 30 + 24 = 364

SELECT count(distinct user_id) FROM recommendation.taste_preferences where item_id=160;  --  = 80
SELECT count(distinct user_id) FROM recommendation.taste_preferences where item_id=266;  --  = 30
SELECT count(distinct user_id) FROM recommendation.taste_preferences; -- = 450


SELECT count(distinct item_id) FROM recommendation.taste_preferences; -- = 912tag_to_tag_similarity_loglikelihood


Select A.user_id from taste_preferences A 
	inner join (select B.user_id from taste_preferences B where B.item_id = 160) as aliasB on A.user_id = aliasB.user_id where A.item_id = 266 order by A.user_id asc;
-- = 24 votes occured together


Select count(A.user_id) from taste_preferences A 
	inner join (select B.user_id from taste_preferences B where B.item_id = 1) as aliasB on A.user_id = aliasB.user_id where A.item_id = 2 order by A.user_id asc;
-- = 5
SELECT count(distinct user_id) FROM recommendation.taste_preferences where item_id=1;  --  = 19
SELECT count(distinct user_id) FROM recommendation.taste_preferences where item_id=2;  --  = 23
