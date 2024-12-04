package com.divyajyoti.user_management.rests.controllers;

import com.divyajyoti.user_management.dtos.ResponseStatusDto;
import com.divyajyoti.user_management.dtos.UserDto;
import com.divyajyoti.user_management.services.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Slf4j
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

    @PostMapping("/user-details-list/by-contact")
    public ResponseEntity<ResponseStatusDto> getUserDetailsListByContacts(@RequestBody List<String> contactsList){
        ResponseStatusDto responseStatusDto = userManagementService.getUserDetailsListByContacts(contactsList);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.FOUND);
    }

    @PostMapping("/user-details-list/by-id")
    public ResponseEntity<ResponseStatusDto> getUserDetailsListByIds(@RequestBody List<String> idsList){
        ResponseStatusDto responseStatusDto = userManagementService.getUserDetailsListByIds(idsList);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.FOUND);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ResponseStatusDto> deleteUser(@PathVariable BigInteger id){
        return null;
    }

}
