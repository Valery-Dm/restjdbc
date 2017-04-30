-- DROP OLD SCHEMA

DROP SCHEMA IF EXISTS `users_demo` ^;

-- CREATE NEW ONE

CREATE SCHEMA `users_demo` ^;

-- ADD ROLE TABLE

CREATE TABLE `users_demo`.`ROLE` (
  `SHORT_NAME` VARCHAR(3) NOT NULL,
  `FULL_NAME`  VARCHAR(50) NOT NULL,
  PRIMARY KEY (`SHORT_NAME`)) ^;


-- ADD USER TABLE

CREATE TABLE `users_demo`.`USER` (
  `ID` BIGINT NOT NULL AUTO_INCREMENT,
  `EMAIL_ADRS`  VARCHAR(70) NOT NULL,
  `FIRST_NAME`  VARCHAR(45) NOT NULL,
  `LAST_NAME`   VARCHAR(70) NOT NULL,
  `MIDDLE_NAME` VARCHAR(45) NULL,
  `PASSWORD`    CHAR(60)    NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `EMAIL_ADRS_UNIQUE` (`EMAIL_ADRS` ASC)) ^;


-- ADD ROLE_USERS TABLE

CREATE TABLE `users_demo`.`ROLE_USERS` (
  `ROLE_ID` VARCHAR(3) NOT NULL,
  `USER_ID` BIGINT NOT NULL,
  PRIMARY KEY (`ROLE_ID`, `USER_ID`),
  UNIQUE INDEX `UQ_USER_ROLE` (`USER_ID`, `ROLE_ID`),
  CONSTRAINT `FK_USER_ID`
    FOREIGN KEY (`USER_ID`)
    REFERENCES `users_demo`.`USER` (`ID`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_ROLE_ID`
    FOREIGN KEY (`ROLE_ID`)
    REFERENCES `users_demo`.`ROLE` (`SHORT_NAME`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION) ^;

