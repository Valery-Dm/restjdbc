USE `users_demo`;

INSERT INTO 
  `USER` (
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

SELECT LAST_INSERT_ID();
