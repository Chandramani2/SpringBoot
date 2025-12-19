package com.rideapps.rider.controller;


import com.rideapps.common.model.dto.ApiResponse;
import com.rideapps.rider.model.User;
import com.rideapps.rider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUser(){

        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", userService.getAllUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(true, "User not found", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));
    }


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody User user){
        userService.registerUser(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "User Registered successfully", null));
    }

    @PostMapping("/createList")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody List<User> userList){
        userService.registerUserList(userList);
        return ResponseEntity.ok(new ApiResponse<>(true, "All User Registered successfully", null));
    }

}