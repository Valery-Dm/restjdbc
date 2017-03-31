
SELECT `users_demo`.`USER`.EMAIL_ADRS FROM 
((`users_demo`.`ROLE_USERS` 
   INNER JOIN `users_demo`.`ROLE` 
   ON `users_demo`.`ROLE_USERS`.ROLE_ID = `users_demo`.`ROLE`.SHORT_NAME)
   INNER JOIN `users_demo`.`USER` 
   ON `users_demo`.`ROLE_USERS`.USER_ID = `users_demo`.`USER`.ID);


SELECT SHORT_NAME, FULL_NAME FROM `users_demo`.`ROLE` WHERE SHORT_NAME = 'ADM';


SET FOREIGN_KEY_CHECKS = 0; 
TRUNCATE users_demo.USER;
SET FOREIGN_KEY_CHECKS = 1;

TRUNCATE users_demo.ROLE_USERS;

SELECT 
  `users_demo`.`USER`.EMAIL_ADRS,
  `users_demo`.`USER`.FIRST_NAME,
  `users_demo`.`USER`.LAST_NAME,
  `users_demo`.`USER`.MIDDLE_NAME
FROM `users_demo`.`USER`
WHERE `users_demo`.`USER`.ID IN
(
  SELECT `users_demo`.`ROLE_USERS`.USER_ID FROM `users_demo`.`ROLE_USERS` 
  WHERE `users_demo`.`ROLE_USERS`.ROLE_ID = 'ADM'
);

