--Database Name:
CREATE DATABASE cyplan;

USE cyplan;
GRANT ALL PRIVILEGES ON cyplan.* TO 'COMS'@'localhost' IDENTIFIED BY 'coms';
-- USER TABLE STRUCTURE:
-- drop table users;
-- CREATE TABLE users(
--     user_id INT NOT NULL AUTO_INCREMENT,
--     first_name VARCHAR(50) NOT NULL,
--     email VARCHAR(250) NOT NULL UNIQUE ,
--     major varchar(20),
--     user_type varchar(50),
--     password VARCHAR(50) NOT NULL,
--     PRIMARY KEY (user_id)
-- );
-- select * from users;