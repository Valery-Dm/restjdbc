SELECT 
	user.`ID`,
    user.`EMAIL_ADRS`,
    user.`FIRST_NAME`,
    user.`LAST_NAME`,
    user.`MIDDLE_NAME`
FROM 
	`users_demo`.`USER` user
INNER JOIN 
	`users_demo`.`ROLE_USERS` roles
ON 
	user.`ID` = roles.`USER_ID`
AND 
	roles.`ROLE_ID` = ?;
