DROP VIEW IF EXISTS users_preferences;
DROP TABLE IF EXISTS POST_TAG;
DROP TABLE IF EXISTS TAGS;
DROP TABLE IF EXISTS COMMENTS;
DROP TABLE IF EXISTS VOTES;
DROP TABLE IF EXISTS POSTS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS tag_to_tag_similarity_mahout;
DROP TABLE IF EXISTS tag_to_tag_similarity_cosine;
DROP TABLE IF EXISTS tag_to_tag_similarity_loglikelihood;

CREATE TABLE USERS (
  ID               BIGINT   NOT NULL PRIMARY KEY,
  VERSION          BIGINT   NOT NULL,
  REPUTATION       INT      NOT NULL,
  CREATION_DATE    DATETIME NOT NULL,
  DISPLAY_NAME     VARCHAR(255),
  LAST_ACCESS_DATE DATETIME NOT NULL,
  LOCATION         VARCHAR(255),
  ABOUT            TEXT,
  VIEWS            INT      NOT NULL,
  UP_VOTES         INT      NOT NULL,
  DOWN_VOTES       INT      NOT NULL
)
  ENGINE = InnoDB;

CREATE TABLE POSTS (
  ID                 BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  VERSION            BIGINT       NOT NULL,
  POST_TYPE          INT          NOT NULL,
  ACCEPTED_ANSWER_ID BIGINT,
  CREATION_DATE      DATETIME,
  SCORE              INT,
  VIEW_COUNT         INT          NOT NULL,
  BODY               TEXT,
  OWNER_USER_ID      BIGINT       NOT NULL,
  TITLE              VARCHAR(255) NULL,
  ANSWER_COUNT       INT,
  COMMENT_COUNT      INT,
  FAVORITE_COUNT     INT,
  PARENT_ID          BIGINT,
  CONSTRAINT POST_USER FOREIGN KEY (OWNER_USER_ID) REFERENCES USERS (ID)
)
  ENGINE = InnoDB;

CREATE TABLE VOTES (
  ID            BIGINT   NOT NULL PRIMARY KEY,
  VERSION       BIGINT   NOT NULL,
  POST_ID       BIGINT   NOT NULL,
  VOTE_TYPE     INT      NOT NULL,
  CREATION_DATE DATETIME NOT NULL,
  CONSTRAINT VOTE_POST FOREIGN KEY (POST_ID) REFERENCES POSTS (ID)
)
  ENGINE = InnoDB;

CREATE TABLE COMMENTS (
  ID            BIGINT NOT NULL PRIMARY KEY,
  VERSION       BIGINT NOT NULL,
  POST_ID       BIGINT NOT NULL,
  VALUE         TEXT,
  CREATION_DATE DATETIME,
  USER_ID       BIGINT NOT NULL,
  SCORE         INT,
  CONSTRAINT COMMENTS_POST FOREIGN KEY (POST_ID) REFERENCES POSTS (ID),
  CONSTRAINT USERS_POST FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
)
  ENGINE = InnoDB;

CREATE TABLE TAGS (
  ID      BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  VERSION BIGINT       NOT NULL,
  TAG     VARCHAR(255) NOT NULL,
  CONSTRAINT tag_unique UNIQUE (TAG)
)
  ENGINE = InnoDB;

CREATE TABLE POST_TAG (
  POST_ID BIGINT NOT NULL,
  TAG_ID  BIGINT NOT NULL,
  CONSTRAINT POST_TAG_POST FOREIGN KEY (POST_ID) REFERENCES POSTS (ID),
  CONSTRAINT POST_TAG_TAG FOREIGN KEY (TAG_ID) REFERENCES TAGS (ID)
)
  ENGINE = InnoDB;

CREATE VIEW users_preferences AS
  SELECT
    pp.owner_user_id           AS user_id,
    t.id                       AS item_id,
    SUM(coalesce(pp.score, 1)) AS preference
  FROM TAGS t INNER JOIN
    post_tag tp ON t.id = tp.tag_id
    LEFT OUTER JOIN
    posts p ON tp.post_id = p.id
    INNER JOIN
    posts pp ON pp.parent_id = p.id
  WHERE pp.post_type = 2
  GROUP BY PP.OWNER_USER_ID, T.ID
  ORDER BY NULL;

CREATE TABLE tag_to_tag_similarity_mahout (
  tag_id_a  BIGINT NOT NULL,
  tag_id_b  BIGINT NOT NULL,
  similarity FLOAT  NOT NULL,
  PRIMARY KEY (tag_id_a, tag_id_b)
)
  ENGINE = InnoDB;

CREATE TABLE tag_to_tag_similarity_cosine (
  tag_id_a  BIGINT NOT NULL,
  tag_id_b  BIGINT NOT NULL,
  similarity FLOAT  NOT NULL,
  PRIMARY KEY (tag_id_a, tag_id_b)
)
  ENGINE = InnoDB;

CREATE TABLE tag_to_tag_similarity_loglikelihood (
  tag_id_a  BIGINT NOT NULL,
  tag_id_b  BIGINT NOT NULL,
  similarity FLOAT  NOT NULL,
  PRIMARY KEY (tag_id_a, tag_id_b)
)
  ENGINE = InnoDB;

CREATE INDEX POST_TYPE
  ON POSTS (POST_TYPE);
CREATE INDEX POST_CREATION_DATE
  ON POSTS (CREATION_DATE);
CREATE INDEX POST_PARENT_ID
  ON POSTS (PARENT_ID);
CREATE INDEX POST_OWNER_ID
  ON POSTS (OWNER_USER_ID);