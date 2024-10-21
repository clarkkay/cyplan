CREATE TABLE users(
	username varchar(30),
    password varchar(30),
    userType varchar(1), -- A=advisor S=student  L=Admin/leader/overlord
    PRIMARY KEY (username)
);

CREATE TABLE courses(
	courseCode varchar(12),
    courseName varchar(70),
    preReqs varchar(70),
    coReqs varchar(70),
    credits int,
    semesterOffered varchar(10),
    PRIMARY KEY (courseCode)
);

CREATE TABLE registers(
	username varchar(30),
    courseCode varchar(30),
    semesterTaken varchar(20),
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (courseCode) REFERENCES courses(courseCode)
);

CREATE TABLE degrees(
	name varchar(30),
	college varchar(30),
    level varchar(3), -- MS, PhD, BS.   does this also deal with majors and minors?
    PRIMARY KEY (name,level)
);

CREATE TABLE requirements(
	degree varchar(30),
    courseCode varchar(12),
    fulfills varchar(15), -- this deals with how it goes towards your major (like is it an SE elective, or a core class, etc.)
    FOREIGN KEY (degree) REFERENCES degrees(name),
    FOREIGN KEY (courseCode) REFERENCES courses(courseCode)
);

-- removes courses

DROP TABLE requirements;
DROP TABLE degrees;
DROP TABLE registers;
DROP TABLE courses;
DROP TABLE users; 

