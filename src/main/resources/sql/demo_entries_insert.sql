-- 3 predefined roles

INSERT INTO `users_demo`.`ROLE` (`SHORT_NAME`, `FULL_NAME`)
VALUES ('ADM', 'Administrator'), ('USR', 'User'), ('DEV', 'Developer');

-- 5 demo users with different roles (password is 123456)

INSERT INTO `users_demo`.`USER` (
  `EMAIL_ADRS`, `FIRST_NAME`, `LAST_NAME`, `MIDDLE_NAME`, `PASSWORD`)
VALUES 
  ('demo.user@spring.demo', 'Ivan', 'Ivanov', 'Ivanovich', '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.developer@spring.demo', 'Sergey', 'Sergeev', 'Sergeevich', '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.admin@spring.demo', 'Alexey', 'Alexeev', 'Alexeevich', '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.admin.dev@spring.demo', 'Vasily', 'Vasiljev', '', '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG'),
  ('demo.user.dev@spring.demo', 'Michail', 'Michailov', '', '$2a$10$.UMMd6qYlqrPPmEr7bVk5.PHur8JnV3d8ir7QUKOy.hwumE9HQWKG');

-- their roles

INSERT INTO `users_demo`.`ROLE_USERS` (`ROLE_ID`, `USER_ID`)
VALUES ('USR', 1), ('DEV', 2), ('ADM', 3), ('DEV', 4), ('ADM', 4), ('USR', 5), ('DEV', 5);
