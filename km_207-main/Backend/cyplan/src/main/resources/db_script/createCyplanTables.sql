CREATE TABLE degrees(
    degree_name varchar(30) UNIQUE, -- Computer Science, Software Engineering, Chemistry, ...
    major varchar(15) UNIQUE, -- COM S, S E, CHEM, ...
    college varchar(30),
    PRIMARY KEY (major)
);

CREATE TABLE users(
    user_id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    email VARCHAR(250) NOT NULL UNIQUE ,
    major varchar(15),
    user_type varchar(50),
    password VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id),
    FOREIGN KEY (major) REFERENCES degrees(major)
);

CREATE TABLE courses(
    course_code varchar(12) NOT NULL,       -- COM S 127, SE 185, CPRE 281, ...
    course_name varchar(70) NOT NULL,		-- the actual title given to the course
    description varchar(1000) NOT NULL,		-- the description of the course in the catalog (can just put "DESC" for times sake and we can go back in and change it later)
    prereqs varchar(100),					-- list of prereqs. seperate distinct prereqs with commas (spaces dont matter) and coprereqs with a semicolon (no spaces between) [i.e.  COM S 327, COM S 321;CPRE 381, COM S 314]
    coreqs varchar(100),					-- list of coreqs. Same rules as prereqs above.
    credits int,							-- number of credits the course is worth. If variable credits (like 2-3, just choose one and put a comment next to it describing the situation)
    semester_offered varchar(8),			-- list of semesters that the course is offered. comma seperated, no spaces. F = fall, S = Spring, SS = summer, W = winter (i.e. "F,S,SS,W")
    total_rating float,						-- currently unused. when adding records, make this 0
    num_ratings int,						-- currently unused. when adding records, make this 0
    PRIMARY KEY (course_code)
);

CREATE TABLE plans(
    user_id int,
    course_code varchar(30),
    semester_taken int NOT NULL, -- semester will begin on the "since" date that a student enrolled in a degree. so if they enrolled in their degree in Fall2022, then 1=Fall2022, 2=Winter2022, 3=Spring2023, 4=Summer2023, etc)
    plan_id int,
    plan_name varchar(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (course_code) REFERENCES courses(course_code)
);

CREATE TABLE requirements(
    major varchar(15),
    req_code int,                                   -- Paired with the major, this number is a Unique identifier of the given requirement (req code 5 means one thing for SE degree but another thing for MATH). Requirement examples: SE elective, MATH elective, required/core class, etc.
    req_name varchar(30),                           -- this is for humans to know what the reqCode means when with each course (so like the row with degree as COMS with req code 5 is the 'math elective' or something
    to_fulfill varchar(20),                         -- This will tell how many credits or courses are needed to fulfull the requirment Something like (1 course) or (3 credit)
    to_fulfill_type varchar(6),                     -- This tells you if the "toFulfill" is the number of COURSES you need or number of CREDITS (some requirements require a certain number of courses while others require a certain number of credits)
    PRIMARY KEY (major, req_code),
    FOREIGN KEY (major) REFERENCES degrees(major)
);

-- Table to tell how certain courses count towards requirements for certain majors
CREATE TABLE fulfillments(
    major varchar(15),					-- identifies which major the given requirment fulfillment refers to (becuase the primary key of the requirements table is a major AND a req_code
    course_code varchar(12),					-- the code of the course in question
    req_code int,						-- paired with the major, this tells which requirement the course is fulfilling
    PRIMARY KEY (major, course_code, req_code),
    FOREIGN KEY (major, req_code) REFERENCES requirements(major, req_code),
    FOREIGN KEY (course_code) REFERENCES courses(course_code)
);

-- table to store the specific courses and semester they are reccomended to be taken based on the base plans given on the ISU website
CREATE TABLE basePlanCourses(
    major varchar(15),
    course_code varchar(12),
    semester_taken int,									-- the semester that the given course is said to be taken in the base plan (semesters range from 1 to 8)
    FOREIGN KEY (major) REFERENCES degrees(major),
    FOREIGN KEY (course_code) REFERENCES courses(course_code),
    PRIMARY KEY (major, course_code)
);

-- table to store the "optional/choice" courses that students can choose (like "Social sciences", or "Math electives") and the semester the choice is given on the base plan on the ISU website
CREATE TABLE basePlanClusters(
    major varchar(15),
    req_codes varchar(15),			-- requirement that the base plan is giving the student courses to choose from (sometimes its not a single requirement, but rather multiple, like Social science OR international perspective OR USD, so this is a string of rec codes SEPERARED BY COMMA NO SPACE. If you need courses that cover MULTIPLE requirements (like social science AND IP, then there is a semicolon between the reqs (like req code for SS=1 & IP=2, so it would be 1;2)
    semester_taken int,				-- the semester that the choice is being offered (semesters range from 1 to 8)
    FOREIGN KEY (major) REFERENCES degrees(major),
    PRIMARY KEY (major, req_codes, semester_taken)
);

CREATE TABLE comments(
    course_code varchar(30),
    user_comment varchar(250),
    PRIMARY KEY (course_code, user_comment),
    FOREIGN KEY (course_code) REFERENCES courses(course_code)
);

CREATE TABLE friends(
    friend_id INT NOT NULL AUTO_INCREMENT,
    currentUseremail VARCHAR(250) NOT NULL,
    friendEmail VARCHAR(250),
    status varchar(20) not null,
    pendingFriendEmail VARCHAR(250),
    PRIMARY KEY (friend_id)
);
create table advisorChats(
    chat_id INT NOT NULL auto_increment,
    plan_name varchar(250) NOT NULL,
    advisorEmail varchar(250) NOT NULL,
    adviseeEmail varchar(250) NOT NULL,
    chat varchar(500),
    primary key (chat_id)
);
