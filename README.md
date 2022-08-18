# shareUs
Hello, this is a project using frameworks. Registration contains captcha, mail confirmation, password encoding.
This is a message service, where you can post your messages, open author's profiles, subscribe and
like their posts. You can log in as admin and edit or delete the other users.
## Used technologies
Spring Boot, Spring MVC, Spring Data JPA, Spring Security, Hibernate, MySQL, SQL, HQL, Recaptcha, Mail Sender, Validation, JUNIT, Module Tests, UNIT tests, Mockito, Mock Mvc, Thymeleaf, HTML, CSS, Docker, Lombok, RememberMe, FlyWay, Turbolinks, Pagination, Postman.
## Requirements
+ Git  
+ Docker  
+ Docker-compose  
## Run
```
$ git clone https://github.com/MatveyAndrosyukk/shareUs.git
$ cd shareUs
$ docker pull wouterpolet/maven-jdk-15
$ docker build -t message_service .  
$ docker-compose up -d     
```
## Stop
Press **Ctrl+C** to stop containers running

## Basic Workflow
1. Move http://localhost:8080/registration and register here. 
2. Confirm your email.  
3. Go to login page and log in. 
4. Move to a messages page and use site with pleasure.  
5. Log in as admin and edit or delete the other users.

## Additional information
1. If you don't want to register, you can use one of pre created users to log in:
+ admin - admin
+ user - user
2. MySQL container can create database for a long time, so you need to wait some time.
3. Captcha can not be rendered from the first time, so you need to refresh the page.
