# recommendation-engine
Thesis for pHD
- technology stack :
  - spring boot
  - apache hadoop


Creating the export job :
- From Spring-XD
1. Export the sql data to hadoop hdfs in order to be able to process it by the recommendation job
  job create hdfsImport --definition "jdbchdfs --sql='select post.owner_user_id, tag.id, coalesce(post.score, 1) from tags tag inner join post_tag posttag on tag.id = posttag.tag_id left outer join posts post2 on posttag.post_id = post2.id inner join posts post on post.parent_id = post2.id where post.post_type = 2 order by post.owner_user_id'" --deploy
  job launch hdfsImport
2. Trigger the mapreduce job by creating the mahout recommender custom job
  job create mahout --definition "recommender" --deploy
  job launch mahout
3. Export the data from hadoop hdfs back to sql

