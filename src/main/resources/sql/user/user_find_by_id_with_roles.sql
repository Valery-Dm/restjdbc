USE `users_demo`;

SET @user_id = ?;

SELECT 
    `ID`,
    `EMAIL_ADRS`,
    `FIRST_NAME`,
    `LAST_NAME`,
    `MIDDLE_NAME`
FROM 
    `USER` 
WHERE 
    `ID` = @user_id;

SELECT 
    `SHORT_NAME`, 
    `FULL_NAME` 
FROM 
    `ROLE`
INNER JOIN 
    `ROLE_USERS`
ON 
    `ROLE_ID` = `SHORT_NAME`
AND 
    `USER_ID` = @user_id;

