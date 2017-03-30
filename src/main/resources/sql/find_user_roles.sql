SELECT 
  `users_demo`.`ROLE`.SHORT_NAME, 
  `users_demo`.`ROLE`.FULL_NAME 
FROM `users_demo`.`ROLE`
WHERE `users_demo`.`ROLE`.SHORT_NAME IN 
(
  SELECT `users_demo`.`ROLE_USERS`.ROLE_ID
  FROM `users_demo`.`ROLE_USERS` 
  WHERE `users_demo`.`ROLE_USERS`.USER_ID = ?
);
