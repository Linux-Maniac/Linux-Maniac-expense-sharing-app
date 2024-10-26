package com.divyajyoti.user_management.service;

import com.divyajyoti.user_management.dto.ResponseStatusDto;
import com.divyajyoti.user_management.dto.UserDto;
import com.divyajyoti.user_management.entity.UserEntity;
import com.divyajyoti.user_management.repository.UserManagementRepository;
import com.divyajyoti.user_management.rest.exception.GenericRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Slf4j
@Service
public class UserManagementService {

    private final UserManagementRepository userManagementRepository;

    @Autowired
    public UserManagementService(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }

    @CachePut(value = "expense-sharing-user-management", key = "(#userData.name + '-' + #userData.contact).toUpperCase()")
    public ResponseStatusDto registerNewUser(UserDto userData) {
        Optional<UserEntity> optionalUser;
        try {
            optionalUser = userManagementRepository.findByContact(userData.getContact());
        } catch (Exception e) {
            throw new GenericRestException("DATABASE ERROR, PLEASE TRY LATER!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalUser.isPresent())
            throw new GenericRestException("USER ALREADY EXISTS WITH GIVEN CONTACT NO!", HttpStatus.BAD_REQUEST);
        UserEntity newUser = new UserEntity();
        newUser.setContact(userData.getContact());
        newUser.setName(userData.getName());
        newUser.setEmail(userData.getEmail());
        UserEntity savedUser;
        try {
            savedUser = userManagementRepository.save(newUser);
        } catch (Exception e) {
            throw new GenericRestException("ERROR WHILE SAVING INFO INTO DATABASE!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseStatusDto("USER CREATION SUCCESSFUL", savedUser);
    }

    @CachePut(value = "expense-sharing-user-management", key = "(#userData.name + '-' + #userData.contact).toUpperCase()")
    public ResponseStatusDto updateUserDetails(UserDto userData, BigInteger id) {
        Optional<UserEntity> optionalUser;
        try {
            optionalUser = userManagementRepository.findById(id);
        } catch (Exception e) {
            log.error("DB_FETCH_ERR: {}", e.getMessage());
            throw new GenericRestException("DATABASE ERROR, PLEASE TRY LATER!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalUser.isEmpty())
            throw new GenericRestException("USER DOES NOT EXIST", HttpStatus.BAD_REQUEST);
        UserEntity newUser = new UserEntity();
        newUser.setId(id);
        newUser.setContact(userData.getContact());
        newUser.setName(userData.getName());
        newUser.setEmail(userData.getEmail());
        UserEntity savedUser;
        try {
            savedUser = userManagementRepository.save(newUser);
        } catch (Exception e) {
            throw new GenericRestException("ERROR WHILE UPDATING INFO INTO DATABASE!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseStatusDto("USER DETAILS UPDATED", savedUser);
    }

    public ResponseStatusDto getUserDetails(BigInteger id) {
        Optional<UserEntity> optionalUser;
        try{
            optionalUser = userManagementRepository.findById(id);
        } catch (Exception e){
            log.error("DATABASE ERROR WHILE GETTING USER DATA: {}", e.getMessage());
            throw new GenericRestException("SERVER ERROR WHILE GETTING USER DATA: {}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(optionalUser.isEmpty())
            throw new GenericRestException("USER DOES NOT EXIST: {}", HttpStatus.NOT_FOUND);
        UserEntity foundUserEntity = optionalUser.get();
        UserDto userDto = new UserDto(foundUserEntity.getName(),
                foundUserEntity.getContact(), foundUserEntity.getEmail());
        return new ResponseStatusDto("SUCCESS", userDto);
    }

}
