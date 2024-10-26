package com.divyajyoti.user_management.rest.controller;

import com.divyajyoti.user_management.dto.ResponseStatusDto;
import com.divyajyoti.user_management.dto.UserDto;
import com.divyajyoti.user_management.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/user-management")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService){
        this.userManagementService = userManagementService;
    }

    @PostMapping("/new-user")
    public ResponseEntity<ResponseStatusDto> registerNewUser(@RequestBody  UserDto userData){
        ResponseStatusDto responseStatusDto = userManagementService.registerNewUser(userData);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.CREATED);
    }

    @PutMapping("/edit-user/{id}")
    public ResponseEntity<ResponseStatusDto> updateUserDetails(@RequestBody UserDto userData, @PathVariable BigInteger id){
        ResponseStatusDto responseStatusDto = userManagementService.updateUserDetails(userData, id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.OK);
    }

    @GetMapping("/user-details/{id}")
    public ResponseEntity<ResponseStatusDto> getUserDetails(@PathVariable BigInteger id){
        ResponseStatusDto responseStatusDto = userManagementService.getUserDetails(id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.FOUND);
    }

}
