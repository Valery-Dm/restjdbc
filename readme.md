## Active directory service
Technologies used: Spring Boot (on Tomcat), Spring REST, JDBC, MySQL

Not yet implemented: Spring Security 

### Initial task:
Create REST service on Spring with following operations:
- Create user
- Retrieve user
- Update user
- Delete user
- Get a list of user with some Role 

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
- Role|Roles (optional)

No ORM allowed for Persistence layer

### Usage:
Download jar file (latest version) from root directory.
And run it from command line 
> java -jar restjdbc-{version}.jar 

Then, open browser.
Swagger documentation for REST API is available at:

Swagger UI
> http://localhost:8080/swagger-ui.html

Swagger REST
> http://localhost:8080/v2/api-docs
