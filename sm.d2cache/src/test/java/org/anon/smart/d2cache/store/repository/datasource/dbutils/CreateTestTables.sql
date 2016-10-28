DROP TABLE tt_simple;
DROP TABLE tt_complex;
DROP TABLE tt_related;
DROP TABLE tt_another;
DROP TABLE tt_simple_complex;

CREATE TABLE tt_simple (
    persist1 VARCHAR(32) NOT NULL PRIMARY KEY,
    persist2 INTEGER,
    another VARCHAR(32)
);

CREATE TABLE tt_complex(
    complexId INTEGER NOT NULL PRIMARY KEY,
    complexData VARCHAR(32)
);

CREATE TABLE tt_related (
    relatedId INTEGER NOT NULL PRIMARY KEY,
    relatedStr VARCHAR(32),
    complexId INTEGER
);

CREATE TABLE tt_another (
    anotherId INTEGER NOT NULL PRIMARY KEY,
    anotherstr VARCHAR(32),
    complexId INTEGER
);

CREATE TABLE tt_simple_complex (
    cpersist1 VARCHAR(32) NOT NULL PRIMARY KEY,
    cpersist2 INTEGER,
    cpersist3 VARCHAR(32)
);

