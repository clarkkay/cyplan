package com.km207.cyplan.services.userServices;

import com.km207.cyplan.models.user;
import com.km207.cyplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class getService {
//get class to return string objects of first_name and major
        @Autowired
        private UserRepository userRepository;

        public Optional<Integer> findIDByEmail(String email) {
                return userRepository.findByEmail(email).map(user::getUser_id);
        }

        // Get user information
        public Optional<String> findFirstNameByEmail(String email) {
            return userRepository.findByEmail(email).map(user::getFirst_name);
        }
        public Optional<String> findMajorByEmail(String email) {
                return userRepository.findByEmail(email).map(user::getMajor);
        }
}
