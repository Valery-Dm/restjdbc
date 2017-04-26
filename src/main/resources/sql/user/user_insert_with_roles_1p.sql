USE `users_demo`;

INSERT INTO 
  `USER` 
    (
    `EMAIL_ADRS`, 
    `FIRST_NAME`, 
    `LAST_NAME`, 
    `MIDDLE_NAME`, 
    `PASSWORD`
     ) 
VALUES 
  (
    ?, 
    ?, 
    ?, 
    ?, 
    ?
  );

SET @user_id = LAST_INSERT_ID();


INSERT INTO 
    `ROLE_USERS` (`ROLE_ID`, `USER_ID`) 
SELECT 
    `SHORT_NAME`, @user_id
FROM 
    `ROLE`
WHERE 
    `SHORT_NAME` 
IN 
    (
