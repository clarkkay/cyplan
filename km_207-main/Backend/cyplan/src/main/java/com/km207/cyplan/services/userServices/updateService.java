package com.km207.cyplan.services.userServices;

import com.km207.cyplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class updateService {
    @Autowired
    UserRepository userRepository;
    //Update Service
    public int updateUserServiceMethod(String email, String first_name, String major){
        return userRepository.updateUserInformation(email, first_name, major);
    }
    //Get Service

}
