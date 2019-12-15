CREATE DATABASE IF NOT EXISTS epam;
USE epam;
CREATE TABLE IF NOT EXISTS
    users
(
    id            INT(4) PRIMARY KEY  NOT NULL,
    name          VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    bank          INT(4),
    authority_lvl INT(4)
);
CREATE TABLE IF NOT EXISTS
    horses
(
    id           INT(4) PRIMARY KEY NOT NULL,
    name         VARCHAR(32) UNIQUE NOT NULL,
    wins_counter INT(4)
);
CREATE TABLE IF NOT EXISTS
    races
(
    id        INT(4) PRIMARY KEY NOT NULL,
    race_date VARCHAR(32),
    status    INT(1),
    winner    VARCHAR(32),
    FOREIGN KEY (winner) REFERENCES horses (name)
);
CREATE TABLE IF NOT EXISTS
    racehorses
(
    race_id    INT(4)      NOT NULL,
    horse_name varchar(32) NOT NULL,
    FOREIGN KEY (race_id) REFERENCES races (id) ON DELETE CASCADE,
    FOREIGN KEY (horse_name) REFERENCES horses (name) ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS
    bets
(
    id         INT(4) PRIMARY KEY NOT NULL,
    amount     FLOAT,
    race_id    INT(4),
    horse_name VARCHAR(32),
    user_name  VARCHAR(255),
    FOREIGN KEY (race_id) REFERENCES races (id) ON DELETE RESTRICT,
    FOREIGN KEY (horse_name) REFERENCES horses (name) ON DELETE RESTRICT,
    FOREIGN KEY (user_name) REFERENCES users (name) ON DELETE CASCADE
);