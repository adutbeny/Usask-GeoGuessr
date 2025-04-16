CREATE DATABASE IF NOT EXISTS myDB;

USE myDB;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    N_high_score INT DEFAULT 0,
    S_high_score INT DEFAULT 0,
    E_high_score INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS userhscorehistory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    score INT DEFAULT 0,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userpinned (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    pinned VARCHAR(255) NOT NULL,
    latitude FLOAT(18, 15),
    longitude FLOAT(18, 15),
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userhistory(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    userdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    userdifficulty VARCHAR(20) NOT NULL,
    gamescore INT DEFAULT 0,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);