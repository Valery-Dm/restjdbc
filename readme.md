## User's directory Restful web-service
Technologies used: Spring Boot (on Tomcat), Spring Security, Spring REST, 
JDBC, MySQL (vesrion 5.7), Java (version 1.8)

### Initial task:
Create REST service on Spring with following operations:
- Create user
- Retrieve user
- Update user
- Delete user
- Get a list of users with some role 

All operations can be done by any user with role 'Administrator' only

Service must implement three Roles by default: 
- Administrator
- Developer
- User

User fields are:
- Email (unique identifier, required)
- Password (Auto-generate one if not provided)
- First name (required)
- Last name (required)
- Middle name (optional)
- Role | Roles (optional)

No ORM allowed for Persistence layer

### Usage:
First, prepare local instance of MySQL database (available at localhost:3306).


Create the working schema with tables using 
[this file](src/main/resources/sql/tables_create.sql)

Create new user for this app using 
[this file](src/main/resources/sql/db_user_create.sql)

Insert demo entries using  
[this file](src/main/resources/sql/demo_entries_insert.sql)

Download jar file (latest version) from root directory.
And run it from command line 
> java -jar restjdbc-{version}.jar 

Then open browser.
Swagger documentation for REST API is available at:

Swagger UI
> https://localhost:8443/swagger-ui.html

Swagger REST
> https://localhost:8443/v2/api-docs

#### HTTP Basic Authorization is implemented:

Every operation must be done by the user with Administrator role.

For Swagger UI there will be a standard browser's login window.
In Postman (Chrome's plugin) - on 'Authorization' tab, select 'Basic Auth'
for the field 'Type' and then enter the following:

Username:
> demo.admin@spring.demo

Password:
> 123456

You may create your own user with ADM role granted with the same result.