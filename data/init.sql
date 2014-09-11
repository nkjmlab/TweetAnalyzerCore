DROP TABLE TWEETS IF EXISTS;

CREATE TABLE TWEETS(
    id long PRIMARY KEY, 
    createdAt VARCHAR,
    geoLocation VARCHAR,
    hashtagEntities VARCHAR,
    place VARCHAR,
    user VARCHAR,
    retweetId long, 
    text VARCHAR
);
