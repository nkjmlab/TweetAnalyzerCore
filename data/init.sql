DROP TABLE TWEETS IF EXISTS;

CREATE TABLE TWEETS(
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
