DROP TABLE TWEETS IF EXISTS;

CREATE TABLE TWEETS(
    category VARCHAR DEFAULT 'NONE',
    queryId long DEFAULT -1,
    id long PRIMARY KEY, 
    createdAt TIMESTAMP,
    lat DOUBLE,
    lng DOUBLE,
    place VARCHAR,
    user VARCHAR,
    retweetId long, 
    text VARCHAR,
    hashtagEntities VARCHAR,
);


CREATE TABLE KEIO_NOUNS(WORD VARCHAR PRIMARY KEY);

