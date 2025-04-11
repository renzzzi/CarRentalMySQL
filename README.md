# CarRentalMySQL

Important MySQL reminders:

UPDATE users SET Type = 1 WHERE Email = 'admin';

CREATE TABLE users (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100),
    PhoneNumber VARCHAR(20),
    Password VARCHAR(100),
    Type INT
);

CREATE TABLE cars (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Brand VARCHAR(30),
    Model VARCHAR(30),
    Color VARCHAR(30),
    Year INT,
    Price DOUBLE,
    Available INT
);

CREATE TABLE rents (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    User INT,
    Car INT,
    DateTime DATETIME,
    Hours INT,
    Total DOUBLE,
    Status INT
);