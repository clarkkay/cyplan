package com.km207.cyplan.services.userServices;

import com.km207.cyplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class passwordService {
    @Autowired
    UserRepository userRepository;
    //Update Password Service
    public int updatePassword(String email, String password) {
        return userRepository.updatePassword(email, password);
    }
}
