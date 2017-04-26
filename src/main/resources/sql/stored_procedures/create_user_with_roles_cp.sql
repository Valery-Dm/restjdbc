USE `users_demo` ^;

DROP PROCEDURE IF EXISTS `create_user_with_roles` ^;

CREATE PROCEDURE `create_user_with_roles`(
							in_email VARCHAR(70), 
							in_fname VARCHAR(45),
							in_lname VARCHAR(70), 
							in_mname VARCHAR(45),
							in_pass  CHAR(60),
                            in_roles_num INT,
							IN in_roles VARCHAR(255)
                            )
BEGIN
    IF ( 
	    SELECT NOT EXISTS (
		    SELECT 
				`EMAIL_ADRS` 
			FROM 
				`USER`
		    WHERE 
				`EMAIL_ADRS` = in_email 
            ) 
	    )
    THEN 
		DROP TABLE IF EXISTS 
			user_roles;
		CREATE TEMPORARY TABLE 
			user_roles 
		ENGINE = MEMORY (
			SELECT 
				`SHORT_NAME`, 
                `FULL_NAME` 
			FROM 
				`ROLE`
			WHERE 
				FIND_IN_SET(`SHORT_NAME`, in_roles)
		);
        
        SET @found_roles = ROW_COUNT();
        
        IF 
			@found_roles = in_roles_num
        THEN
			INSERT INTO 
				`USER` (
					`EMAIL_ADRS`, 
					`FIRST_NAME`, 
					`LAST_NAME`, 
					`MIDDLE_NAME`, 
					`PASSWORD`
					) 
			VALUES (
				in_email, 
				in_fname, 
				in_lname, 
				in_mname, 
				in_pass
				);

			SET @user_id = LAST_INSERT_ID();
			
			INSERT INTO 
				`ROLE_USERS` (
					`ROLE_ID`, 
					`USER_ID`
					) 
			SELECT 
				`SHORT_NAME`,
				@user_id
			FROM 
				user_roles;

			SET @added_roles = ROW_COUNT();
		
		ELSE
			SET @added_roles = 0;
		END IF;
	ELSE 
		SET @user_id = null;
    END IF;
END; ^;

