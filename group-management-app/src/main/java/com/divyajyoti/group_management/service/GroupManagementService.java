package com.divyajyoti.group_management.service;

import com.divyajyoti.group_management.dto.GroupDto;
import com.divyajyoti.group_management.dto.UserDto;
import com.divyajyoti.group_management.entity.UserEntity;
import com.divyajyoti.group_management.dto.ResponseStatusDto;
import com.divyajyoti.group_management.entity.GroupEntity;
import com.divyajyoti.group_management.repository.GroupManagementRepository;
import com.divyajyoti.group_management.repository.UserDetailsRepository;
import com.divyajyoti.group_management.rest.exception.GenericRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
public class GroupManagementService {

    private final GroupManagementRepository groupManagementRepository;

    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public GroupManagementService(GroupManagementRepository groupManagementRepository
            , UserDetailsRepository userDetailsRepository) {
        this.groupManagementRepository = groupManagementRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    public ResponseStatusDto createGroup(GroupDto groupData) {
        String setGroupName = groupData.getName();
        Optional<GroupEntity> optionalGroupEntity;
        boolean conflictionGroupFlag = Boolean.FALSE;
        StringBuilder conflictingUserNames = new StringBuilder();
        for(UserDto memberData : groupData.getMembers()){
            try {
                optionalGroupEntity = groupManagementRepository.findGroupByMemberContact(memberData.getContact(), setGroupName);
            } catch (Exception e) {
                log.error("DATABASE_ERR_IN_FETCHING_EXISTING_GROUP_INFO: {}", e.getMessage());
                throw new GenericRestException("DATABASE ERROR, PLEASE TRY LATER!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(optionalGroupEntity.isPresent()){
                GroupEntity foundGroupEntity = optionalGroupEntity.get();
                if(foundGroupEntity.getName().equals(setGroupName)){
                    conflictionGroupFlag = Boolean.TRUE;
                    conflictingUserNames.append(memberData.getName()).append(" ");
                }
            }
        }
        if(conflictionGroupFlag)
            throw new GenericRestException("ERR: SAME GROUP NAME ALREADY EXISTS FOR USERS: " + conflictingUserNames, HttpStatus.BAD_REQUEST);
        GroupEntity groupEntityData = getGroupEntity(groupData);
        GroupEntity savedGroupEntity;
        try {
            savedGroupEntity = groupManagementRepository.save(groupEntityData);
        } catch (Exception e) {
            log.error("DATABASE_ERR_IN_GROUP_DATA_CREATION: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE CREATING GROUP INFO IN DATABASE", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Map<String, Object> details = Map.of("newGroupDetails", savedGroupEntity);
        return new ResponseStatusDto("SUCCESS", "GROUP SUCCESSFULLY CREATED!", details);
    }

    private static GroupEntity getGroupEntity(GroupDto groupData) {
        GroupEntity groupEntityData = new GroupEntity();
        groupEntityData.setName(groupData.getName());
        List<UserEntity> membersDataList = new ArrayList<>();
        UserEntity memberData;
        for(UserDto userData : groupData.getMembers()){
            memberData = new UserEntity();
            memberData.setContact(userData.getContact());
            memberData.setName(userData.getName());
            memberData.setEmail(userData.getEmail());
            membersDataList.add(memberData);
        }
        groupEntityData.setMembers(membersDataList);
        return groupEntityData;
    }

    public ResponseStatusDto getGroupMembers(BigInteger id) {
        Optional<GroupEntity> optionalGroupEntity;
        try{
            log.info("EXECUTING GROUP FETCH QUERY");
            optionalGroupEntity = groupManagementRepository.findById(id);
        } catch (Exception e) {
            log.error("ERR_WHILE_FETCHING_GROUP_DATA_FROM_DATABASE: {}", e.getMessage());
            throw new GenericRestException("DATABASE ERROR, UNABLE TO FETCH GROUP DATA", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(optionalGroupEntity.isEmpty())
            throw new GenericRestException("ERROR: GROUP DOES NOT EXISTS!", HttpStatus.NOT_FOUND);
        Map<String, Object> details = new HashMap<>();
        List<UserDto> groupMembersList = getUserDtos(optionalGroupEntity);
        details.put("membersCount", groupMembersList.size());
        details.put("groupMembers", groupMembersList);
        log.info("RETURNING RESPONSE");
        return new ResponseStatusDto("SUCCESS", "GROUP MEMBERS DETAILS FETCHED!", details);
    }

    private static List<UserDto> getUserDtos(Optional<GroupEntity> optionalGroupEntity) {
        log.info("EXECUTING GET MEMBERS QUERY");
        List<UserEntity> fetcheUserEntitiesList = optionalGroupEntity.get().getMembers();
        List<UserDto> groupMembersList = new ArrayList<>();
        UserDto groupMember;
        UserEntity fetchedUserEntity;
        for (UserEntity userEntity : fetcheUserEntitiesList) {
            fetchedUserEntity = userEntity;
            groupMember = new UserDto();
            groupMember.setName(fetchedUserEntity.getName());
            groupMember.setContact(fetchedUserEntity.getContact());
            groupMember.setEmail(fetchedUserEntity.getEmail());
            groupMembersList.add(groupMember);
        }
        return groupMembersList;
    }

    public ResponseStatusDto addMember(UserDto memberData, BigInteger id) {
        Optional<GroupEntity> optionalGroupEntity;
        try{
            optionalGroupEntity = groupManagementRepository.findById(id);
        } catch (Exception e) {
            log.error("ERR_WHILE_FETCHING_GROUP_DATA_FROM_DATABASE: {}", e.getMessage());
            throw new GenericRestException("DATABASE ERROR, UNABLE TO FETCH GROUP DATA", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(optionalGroupEntity.isEmpty())
            throw new GenericRestException("ERROR: GROUP DOES NOT EXISTS!", HttpStatus.BAD_REQUEST);
        Optional<UserEntity> optionalUser;
        try{
            optionalUser = userDetailsRepository.findByContact(memberData.getContact());
        } catch (Exception e) {
            log.error("ERR_WHILE_FETCHING_USER_DATA_FROM_DATABASE: {}", e.getMessage());
            throw new GenericRestException("DATABASE ERROR, UNABLE TO FETCH USER DATA", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(optionalUser.isEmpty())
            throw new GenericRestException("ERROR: ATTEMPTED USER DOES NOT EXIST!", HttpStatus.BAD_REQUEST);
        GroupEntity existingGroupEntity = optionalGroupEntity.get();
        UserEntity newMemberEntity = optionalUser.get();
        existingGroupEntity.getMembers().add(newMemberEntity);
        GroupEntity updatedGroupEntity;
        try {
            updatedGroupEntity = groupManagementRepository.save(existingGroupEntity);
        } catch (Exception e) {
            log.error("DATABASE_ERR_WHILE_GROUP_MEMBER_ADDITION: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE UPDATING GROUP MEMBERS IN DATABASE", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Map<String, Object> details = getStringObjectMap(updatedGroupEntity);
        return new ResponseStatusDto("SUCCESS", "NEW MEMBER SUCCESSFULLY ADDED TO GROUP!", details);
    }

    private static Map<String, Object> getStringObjectMap(GroupEntity updatedGroupEntity) {
        List<UserDto> updatedGroupUserDtos = new ArrayList<>();
        GroupDto updatedGroupDto = new GroupDto();
        updatedGroupDto.setName(updatedGroupEntity.getName());
        updatedGroupDto.setMembers(updatedGroupUserDtos);
        UserDto groupUserDto;
        for(UserEntity updatedGroupUserEntity : updatedGroupEntity.getMembers()){
            groupUserDto = new UserDto(updatedGroupUserEntity.getName()
                    , updatedGroupUserEntity.getContact(), updatedGroupUserEntity.getEmail());
            updatedGroupUserDtos.add(groupUserDto);
        }
        return Map.of("updatedGroupDetails", updatedGroupDto);
    }

    public ResponseStatusDto removeMember(UserDto memberData, BigInteger id) {
        Optional<GroupEntity> optionalGroupEntity;
        try{
            optionalGroupEntity = groupManagementRepository.findById(id);
        } catch (Exception e){
            log.error("DATABASE_FETCH_ERR_FOR_GROUP_DATA: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE FETCHING GROUP DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(optionalGroupEntity.isEmpty())
            throw new GenericRestException("GROUP DOES NOT EXIST", HttpStatus.BAD_REQUEST);
        GroupEntity foundGroupEntity = optionalGroupEntity.get();
        List<UserEntity> foundUserEntities = foundGroupEntity.getMembers();
        foundUserEntities.removeIf(memberEntity -> memberEntity.getContact().equals(memberData.getContact()));
        GroupEntity updatedGroupEntity;
        try{
            updatedGroupEntity = groupManagementRepository.save(foundGroupEntity);
        } catch (Exception e){
            log.error("DATA_ERROR_WHILE_DELETING_MEMBER_IN_GROUP: {}", e.getMessage());
            throw new GenericRestException("SERVER ERROR WHILE DELETING MEMBER IN GROUP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Map<String, Object> details = getStringObjectMap(updatedGroupEntity);
        return new ResponseStatusDto("SUCCESS", "NEW MEMBER SUCCESSFULLY REMOVED FROM GROUP!", details);
    }

}
