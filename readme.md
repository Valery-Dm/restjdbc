[На Русском языке](readme.ru.md)

## User's directory Restful web-service 

Technologies used: Spring Boot (on Tomcat), Spring Security, Spring REST, 
JDBC, MySQL (vesrion 5.7), Java (version 1.8)

Service is [available online](http://restjdbc-va1ery.rhcloud.com/swagger-ui.html).
Hosted at OpenShift server. Online version run via http (premium account is required for SSL).

See video examples of:

[How to login as administrator](https://youtu.be/hfpowdVp9PU) and check 'get role' endpoint.

[How to enter as user](https://youtu.be/gw7YLq3aGcU). Operations will be restricted.

[How to create a new user](https://youtu.be/D1isfiq7eXI) (logged in as administrator).

Instructions on how to install a local copy are given below.

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

### How to install locally:
First, prepare local instance of MySQL database (available at http://localhost:3306).

You should manually create the user that this Application will use for connection.

Run [this script](src/main/resources/sql/db_user_create.sql) from within MySQL Workbench,

OR type the following in the command line (copy after the prompt sign >):

```
~> mysql -u root -p

Enter password: type root's password

mysql> CREATE USER 'restjdbc'@'localhost' IDENTIFIED BY 'aA123456'; GRANT ALL ON users_demo.* TO 'restjdbc'@'localhost'; FLUSH PRIVILEGES; exit;
```

Existing DB Schema `users_demo` will be dropped and created anew with all the 
tables and demo entries on Application startup (this is a part of demonstration).

Download jar file (latest version) [here](https://www.dropbox.com/sh/e9fv82aeciuzuql/AABnFAJjeEOKMM4Tvv4Wtw08a?dl=0).
And run it from command line:

`~> java -jar restjdbc-{version}.jar` 

Then open a browser.
Swagger documentation for REST API is available at:

Swagger UI
> https://localhost:8443/swagger-ui.html

Swagger REST
> https://localhost:8443/v2/api-docs

There will be a warning about untrusted certificate when entering the site first time.
The certificate is custom made, so the behavior is expected. Allow to continue.

To stop the server type Ctrl+C combination in terminal window or just close the terminal.

#### HTTP Basic Authorization is implemented:

Every operation must be done by the user with role `Administrator`.

For Swagger UI there will be a standard browser's login window.
In Postman (Chrome's plugin) - on 'Authorization' tab, select 'Basic Auth'
for the field 'Type' and then enter the following:

Username:
> demo.admin@spring.demo

Password:
> 123456

#### Side notes:
The service works well with special REST clients applications (like Postman), but
if you are using a browser there could be some problems with Basic Authorization
mechanism. Browsers are too helpful in remembering user credentials, so for
logout from one user account and login to the other you will probably need to 
restart your browser (and even that may not be sufficient as browser can run 
background processes and never actually restarts). 

It is recommended to use browser's Incognito mode when interacting with the service.

The service that are hosted at OpenShift is not guaranteed to run all the time.

As long as mutator methods are available freely through the RESTful endpoints there are
some defensive restrictions applied:

It is impossible to delete or mutate the default admin user with id 3.

And the server will be restarted once a day reverting DB entries to their initial state.