package com.km207.cyplan.services.userServices;

import com.km207.cyplan.models.plan;
import com.km207.cyplan.repository.PlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class searchUserService {

    @Autowired
    private PlanRepository planRepository;
//User Count by Email
    public int countUsersByEmail(String email) {
        return planRepository.countUser(email);
    }

}
