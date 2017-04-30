USE `users_demo`;

CALL create_user_with_roles( ?, ?, ?, ?, ?, ?, ? );

SELECT @user_id;

SELECT @found_roles;

SELECT * FROM user_roles;

DROP TABLE user_roles;
