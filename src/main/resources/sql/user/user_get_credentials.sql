USE `users_demo`;

SET @user_email = ?;

DROP TABLE IF EXISTS 
    user_t;
CREATE TABLE 
    user_t
SELECT 
	`ID`,
    `EMAIL_ADRS`,
    `FIRST_NAME`,
    `LAST_NAME`,
    `MIDDLE_NAME`,
    `PASSWORD`
FROM 
    `USER` 
WHERE 
    `EMAIL_ADRS` = @user_email;
    
SET @user_id = 
    (
        SELECT `ID` FROM user_t
    );

SELECT 
    * 
FROM 
    user_t;

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
    
DROP TABLE user_t;

