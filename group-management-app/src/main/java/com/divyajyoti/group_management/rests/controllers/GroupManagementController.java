package com.divyajyoti.group_management.rests.controllers;

import com.divyajyoti.group_management.dtos.GroupRequestDto;
import com.divyajyoti.group_management.dtos.ResponseStatusDto;
import com.divyajyoti.group_management.dtos.UserDto;
import com.divyajyoti.group_management.services.GroupManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/group-management")
public class GroupManagementController {

    private final GroupManagementService groupManagementService;

    @Autowired
    public GroupManagementController(GroupManagementService groupManagementService) {
        this.groupManagementService = groupManagementService;
    }

    @PutMapping("/new-group")
    public ResponseEntity<?> createGroup(@RequestBody GroupRequestDto groupData) {
        ResponseStatusDto responseStatusDto = groupManagementService.createGroup(groupData);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.CREATED);
    }

    @GetMapping("/group-members/{id}")
    public ResponseEntity<?> getGroupMembers(@PathVariable BigInteger id) {
        ResponseStatusDto responseStatusDto = groupManagementService.getGroupMembers(id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.FOUND);
    }

    @GetMapping("/group-details/{id}")
    public ResponseEntity<?> getGroupDetails(@PathVariable BigInteger id){
        ResponseStatusDto responseStatusDto = groupManagementService.getGroupDetails(id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.FOUND);
    }

    @PostMapping("/new-member/{id}")
    public ResponseEntity<?> addMember(@RequestBody UserDto memberData, @PathVariable BigInteger id) {
        ResponseStatusDto responseStatusDto = groupManagementService.addMember(memberData, id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.OK);
    }

    @DeleteMapping("/remove-member/{id}")
    public ResponseEntity<?> removeMember(@RequestBody UserDto memberData, @PathVariable BigInteger id) {
        ResponseStatusDto responseStatusDto = groupManagementService.removeMember(memberData, id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.OK);
    }

}
