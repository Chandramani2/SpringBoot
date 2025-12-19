package com.rideapps.rider.service;

import com.rideapps.rider.model.User;
import com.rideapps.rider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public void registerUser(User user){
        userRepository.save(user);
    }

    public void registerUserList(List<User> userList){
        userRepository.saveAll(userList);
    }
}
