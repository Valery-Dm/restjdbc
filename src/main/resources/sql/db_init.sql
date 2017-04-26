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
  `ID` INT NOT NULL AUTO_INCREMENT,
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
  `USER_ID` INT NOT NULL,
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

-- DEMO ENTRIES

-- 3 predefined roles

INSERT INTO `users_demo`.`ROLE` (`SHORT_NAME`, `FULL_NAME`)
VALUES ('ADM', 'Administrator'), ('USR', 'User'), ('DEV', 'Developer') ^;

-- 5 demo users with different roles (row passwords are 123456)

INSERT INTO `users_demo`.`USER` (
  `EMAIL_ADRS`, `FIRST_NAME`, `LAST_NAME`, `MIDDLE_NAME`, `PASSWORD`)
VALUES 
  ('demo.user@spring.demo', 'Ivan', 'Ivanov', 'Ivanovich', 
   '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.developer@spring.demo', 'Sergey', 'Sergeev', 'Sergeevich', 
   '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.admin@spring.demo', 'Alexey', 'Alexeev', 'Alexeevich', 
   '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.admin.dev@spring.demo', 'Vasily', 'Vasiljev', '', 
   '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.user.dev@spring.demo', 'Michail', 'Michailov', '', 
   '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG') ^;

-- their roles

INSERT INTO `users_demo`.`ROLE_USERS` (`ROLE_ID`, `USER_ID`)
VALUES ('USR', 1), ('DEV', 2), ('ADM', 3), ('DEV', 4), ('ADM', 4), ('USR', 5), ('DEV', 5) ^;

