USE `users_demo` ^;

DROP PROCEDURE IF EXISTS `update_user_with_roles` ^;

CREATE PROCEDURE `update_user_with_roles`(
							in_id BIGINT, 
							in_fname VARCHAR(45),
							in_lname VARCHAR(70), 
							in_mname VARCHAR(45),
                            in_roles_num INT,
							IN in_roles VARCHAR(255)
                            )
BEGIN
    -- Find given Roles first to avoid 'Table does not exist' error in the main query
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

    -- Now proceed and find given user id
    IF ( 
	    SELECT EXISTS (
		    SELECT 
				@user_id 
			FROM 
				`USER`
		    WHERE 
				`ID` = in_id 
            ) 
	    )
    -- If it's there continue
    THEN 
        -- Check if found Roles number equals to the expected number
        IF 
			@found_roles = in_roles_num
        THEN
            -- Update User fields
			UPDATE  
				`USER`
			SET 
				`FIRST_NAME` = in_fname, 
				`LAST_NAME` = in_lname, 
				`MIDDLE_NAME` = in_mname
			WHERE
				`ID` = @user_id;
            
            -- Remove old Roles and insert new set
			DELETE FROM 
				`ROLE_USERS`
            WHERE 
				`USER_ID` = @user_id;
            
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

		-- Do not update User and Roles if counts are differ
		ELSE
			SET @added_roles = 0;
		END IF;

    -- When id does not exist set its variable to 0
	ELSE 
		SET @user_id = 0;
    END IF;
END; ^;
