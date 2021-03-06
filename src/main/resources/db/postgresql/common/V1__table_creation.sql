
CREATE SEQUENCE user_account_id_seq;
ALTER SEQUENCE user_account_id_seq RESTART WITH 10001001;

CREATE TABLE user_account
(
    id       SERIAL,
    email    varchar NOT NULL,
    password varchar NOT NULL,
    username varchar NOT NULL,
    mobile   varchar,
    active   boolean NOT NULL DEFAULT TRUE,
    CONSTRAINT email_unique UNIQUE (email),
    CONSTRAINT username_unique UNIQUE (username),
    CONSTRAINT mobile_unique UNIQUE (mobile),
    CONSTRAINT user_account_pk PRIMARY KEY (id)
);

CREATE TABLE user_profile
(
    id        BIGINT NOT NULL,
    name      varchar(100),
    city      varchar(75),
    gender    varchar(5),
    join_date timestamp DEFAULT now(),
    CONSTRAINT user_profile_pk PRIMARY KEY (id),
    CONSTRAINT user_profile_fk FOREIGN KEY (id) REFERENCES user_account (id)
);

CREATE TABLE verification_type
(
    id   INT         NOT NULL,
    name VARCHAR(25) NOT NULL,
    CONSTRAINT verification_type_pk PRIMARY KEY (id)
);

CREATE TABLE user_account_verification
(
    user_id  BIGINT NOT NULL,
    type     INT    NOT NULL,
    verified boolean DEFAULT FALSE,
    CONSTRAINT user_account_verification_pk PRIMARY KEY (user_id, type),
    CONSTRAINT user_account_verification_fk FOREIGN KEY (type) REFERENCES verification_type (id)
);

CREATE TABLE account_status
(
    code        INT         NOT NULL,
    description VARCHAR(50) NOT NULL,
    CONSTRAINT account_status_pk PRIMARY KEY (code)
);

CREATE TABLE user_account_status
(
    user_id BIGINT NOT NULL,
    status  int    NOT NULL,
    remarks varchar(100),
    CONSTRAINT user_account_status_pk PRIMARY KEY (user_id),
    CONSTRAINT user_account_status_fk FOREIGN KEY (status) REFERENCES account_status (code)
);

CREATE SEQUENCE video_id_seq;
ALTER SEQUENCE video_id_seq RESTART WITH 1000000001;
CREATE TABLE video
(
    id              SERIAL,
    active          BOOLEAN   default true NOT NULL,
    title           VARCHAR                NOT NULL,
    description     varchar,
    source_video_id varchar,
    date_posted     TIMESTAMP default CURRENT_TIMESTAMP,
    views           INT       default 0,
    owner           BIGINT                 NOT NULL,
    CONSTRAINT video_pk PRIMARY KEY (id)
);

CREATE TABLE video_like
(
    video_id INT,
    user_id  INT,
    CONSTRAINT video_like_pk PRIMARY KEY (video_id, user_id),
    CONSTRAINT video_like_video_fk FOREIGN KEY (video_id) references video (id),
    CONSTRAINT video_like_user_fk FOREIGN KEY (user_id) references user_account (id)
);

CREATE SEQUENCE video_comment_id_seq;
CREATE TABLE video_comment
(
    id          SERIAL,
    comment     varchar              NOT NULL,
    video_id    INT                  NOT NULL,
    date_posted TIMESTAMP,
    likes       INT     default 0,
    user_id     BIGINT               NOT NULL,
    active      BOOLEAN default true NOT NULL,
    CONSTRAINT video_comment_pk PRIMARY KEY (id),
    CONSTRAINT video_comment_video_fk FOREIGN KEY (video_id) REFERENCES video (id)
);

CREATE SEQUENCE article_id_seq;
ALTER SEQUENCE article_id_seq RESTART WITH 1000000001;
CREATE TABLE article
(
    id            SERIAL,
    active        BOOLEAN   default true NOT NULL,
    title         VARCHAR                NOT NULL,
    body          varchar,
    cover_picture varchar,
    date_posted   TIMESTAMP default CURRENT_TIMESTAMP,
    views         INT       default 0,
    owner         BIGINT                 NOT NULL,
    CONSTRAINT article_pk PRIMARY KEY (id)
);

CREATE TABLE article_like
(
    article_id INT,
    user_id    INT,
    CONSTRAINT article_like_pk PRIMARY KEY (article_id, user_id),
    CONSTRAINT article_like_article_fk FOREIGN KEY (article_id) references article (id),
    CONSTRAINT article_like_user_fk FOREIGN KEY (user_id) references user_account (id)
);

CREATE SEQUENCE article_comment_id_seq;
CREATE TABLE article_comment
(
    id          SERIAL,
    comment     varchar              NOT NULL,
    article_id  INT                  NOT NULL,
    date_posted TIMESTAMP,
    likes       INT     default 0,
    user_id     BIGINT               NOT NULL,
    active      BOOLEAN default true NOT NULL,
    CONSTRAINT article_comment_pk PRIMARY KEY (id),
    CONSTRAINT article_comment_article_fk FOREIGN KEY (article_id) REFERENCES article (id)
);

CREATE SEQUENCE discussion_id_seq;
CREATE TABLE discussion
(
    id            SERIAL,
    active        BOOLEAN   default true NOT NULL,
    title         VARCHAR                NOT NULL,
    details       varchar,
    cover_picture varchar,
    date_posted   TIMESTAMP default CURRENT_TIMESTAMP,
    views         INT       default 0,
    owner         BIGINT                 NOT NULL,
    CONSTRAINT discussion_pk PRIMARY KEY (id)
);

CREATE TABLE discussion_like
(
    discussion_id INT,
    user_id       INT,
    CONSTRAINT discussion_like_pk PRIMARY KEY (discussion_id, user_id),
    CONSTRAINT discussion_like_discussion_fk FOREIGN KEY (discussion_id) references discussion (id),
    CONSTRAINT discussion_like_user_fk FOREIGN KEY (user_id) references user_account (id)
);

CREATE SEQUENCE discussion_reply_id_seq;
CREATE TABLE discussion_reply
(
    id            SERIAL,
    reply         varchar              NOT NULL,
    discussion_id INT                  NOT NULL,
    date_posted   TIMESTAMP,
    likes         INT     default 0,
    user_id       BIGINT               NOT NULL,
    active        BOOLEAN default true NOT NULL,
    CONSTRAINT discussion_reply_pk PRIMARY KEY (id),
    CONSTRAINT discussion_reply_discussion_fk FOREIGN KEY (discussion_id) REFERENCES discussion (id)
);

CREATE SEQUENCE post_id_seq;
ALTER SEQUENCE post_id_seq RESTART WITH 90001001;
CREATE TABLE post
(
    id              SERIAL,
    active          BOOLEAN   default true NOT NULL,
    type            INT                    NOT NULL,
    title           VARCHAR                NOT NULL,
    date_posted     TIMESTAMP default CURRENT_TIMESTAMP,
    likes           INT       default 0,
    views           INT       default 0,
    owner           BIGINT                 NOT NULL,
    d_type          VARCHAR                NOT NULL,
    source_video_id varchar,
    text            varchar,
    cover_picture   varchar,
    CONSTRAINT post_pk PRIMARY KEY (id)
);

CREATE TABLE post_type
(
    id   INT,
    type VARCHAR(15),
    CONSTRAINT post_type_pk PRIMARY KEY (id)
);

CREATE SEQUENCE collection_id_seq;
CREATE TABLE collection
(
    id         SERIAL,
    name       VARCHAR NOT NULL,
    about      VARCHAR NOT NULL,
    visibility INT     NOT NULL,
    owner      BIGINT  NOT NULL,
    CONSTRAINT collection_pk PRIMARY KEY (id)
);

CREATE TABLE collection_video
(
    collection_id BIGINT,
    video_id      BIGINT,
    CONSTRAINT collection_video_pk PRIMARY KEY (collection_id, video_id),
    CONSTRAINT collection_video_collection_fk FOREIGN KEY (collection_id) references collection (id),
    CONSTRAINT collection_video_video_fk FOREIGN KEY (video_id) references video (id)
);

CREATE TABLE collection_article
(
    collection_id BIGINT,
    article_id    BIGINT,
    CONSTRAINT collection_article_pk PRIMARY KEY (collection_id, article_id),
    CONSTRAINT collection_article_collection_fk FOREIGN KEY (collection_id) references collection (id),
    CONSTRAINT collection_article_article_fk FOREIGN KEY (article_id) references article (id)
);

CREATE TABLE collection_discussion
(
    collection_id INT,
    discussion_id INT,
    CONSTRAINT collection_discussion_pk PRIMARY KEY (collection_id, discussion_id),
    CONSTRAINT collection_discussion_collection_fk FOREIGN KEY (collection_id) references collection (id),
    CONSTRAINT collection_discussion_discussion_fk FOREIGN KEY (discussion_id) references discussion (id)
);

CREATE TABLE visibility
(
    id         INT         NOT NULL,
    visibility VARCHAR(50) NOT NULL,
    CONSTRAINT visibility_pk PRIMARY KEY (id)
);

CREATE TABLE feed
(
    post_id     INT,
    post_type   INT,
    date_posted timestamp,
    CONSTRAINT feed_pk PRIMARY KEY (post_id, post_type)
);

CREATE TABLE POST_COLLECTION
(
    collection_id BIGINT,
    post_id       BIGINT,
    CONSTRAINT collection_post_pk PRIMARY KEY (collection_id, post_id),
    CONSTRAINT collection_fk FOREIGN KEY (collection_id) references collection (id),
    CONSTRAINT post_fk FOREIGN KEY (post_id) references post (id)
);

