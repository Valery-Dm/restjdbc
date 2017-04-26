-- GET USER WITH OR WITHOUT USER'S ROLES (WITH NULLS). NEED NOT BE ORDERED.

SELECT 
    `USER`.`ID`, 
    `USER`.`EMAIL_ADRS`, 
    `USER`.`FIRST_NAME`, 
    `USER`.`LAST_NAME`, 
    `USER`.`MIDDLE_NAME`,
    `ROLE`.`SHORT_NAME`,
    `ROLE`.`FULL_NAME`
FROM 
    `users_demo`.`USER` 
LEFT JOIN 
    `users_demo`.`ROLE_USERS` 
ON 
    `ROLE_USERS`.`USER_ID` = `USER`.`ID`
LEFT JOIN 
    `users_demo`.`ROLE`
ON 
    `ROLE_USERS`.`ROLE_ID` = `ROLE`.`SHORT_NAME`
WHERE 
    `USER`.`ID` = ?;
