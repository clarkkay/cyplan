package com.km207.cyplan.services.userServices;

import com.km207.cyplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class deleteService {
    @Autowired
    UserRepository userRepository;

    public int deleteUser(String email){
        return userRepository.deleteUser(email);
    }
}
