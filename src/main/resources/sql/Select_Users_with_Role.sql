SELECT 
  `users_demo`.`USER`.EMAIL_ADRS,
  `users_demo`.`USER`.FIRST_NAME,
  `users_demo`.`USER`.LAST_NAME,
  `users_demo`.`USER`.MIDDLE_NAME
FROM `users_demo`.`USER`
WHERE `users_demo`.`USER`.ID IN
(
  SELECT `users_demo`.`ROLE_USERS`.USER_ID FROM `users_demo`.`ROLE_USERS` 
  WHERE `users_demo`.`ROLE_USERS`.ROLE_ID = ?
);
