package com.km207.cyplan;
import com.km207.cyplan.models.*;
import com.km207.cyplan.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
//"com.km207.cyplan.controllers", "com.km207.cyplan.models", "com.km207.cyplan.repository", "com.km207.cyplan.services",
@SpringBootApplication
@ComponentScan(basePackages = {"com.km207.cyplan.controllers",
		"com.km207.cyplan.models",
		"com.km207.cyplan.repository",
		"com.km207.cyplan.services",
		"com.km207.cyplan.websockets" })

public class CyplanApplication {

	public static void main(String[] args) {
		SpringApplication.run(CyplanApplication.class, args);
	}
//uncomment when using api documentation only

//	@Bean
//	CommandLineRunner initUser(UserRepository userRepository) {
//		return args -> {
//			//MUST CHANGE EMAIL EVERY TIME, email is a unique identifier, if used once (ran once) change the email here again
//			user user1 = new user("Michael", "mikeghj@email.edu", "COM S", "S", "1234");
//			user user2 = new user("Kayley", "kayley313212@email.edu", "COM S", "S", "1234");
//			user user3 = new user("John","john1323@email.edu", "COM S", "S", "1234");

//			userRepository.save(user1);
//			userRepository.save(user2);
//			userRepository.save(user3);
//		};
//	}

}
