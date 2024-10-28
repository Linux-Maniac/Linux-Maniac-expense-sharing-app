package com.divyajyoti.group_management.services;

import com.divyajyoti.group_management.dtos.GroupDto;
import com.divyajyoti.group_management.dtos.ResponseStatusDto;
import com.divyajyoti.group_management.dtos.UserDto;
import com.divyajyoti.group_management.entities.GroupEntity;
import com.divyajyoti.group_management.entities.UserEntity;
import com.divyajyoti.group_management.models.GroupModel;
import com.divyajyoti.group_management.models.UserModel;
import com.divyajyoti.group_management.repositories.GroupEntityRepository;
import com.divyajyoti.group_management.repositories.UserEntityRepository;
import com.divyajyoti.group_management.rests.exceptions.GenericRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
public class GroupManagementService {

    private final GroupEntityRepository groupEntityRepository;

    private final UserEntityRepository userEntityRepository;

    @Autowired
    public GroupManagementService(GroupEntityRepository groupEntityRepository
            , UserEntityRepository userEntityRepository) {
        this.groupEntityRepository = groupEntityRepository;
        this.userEntityRepository = userEntityRepository;
    }

    public ResponseStatusDto createGroup(GroupDto groupData) {
        String setGroupName = groupData.getName();
        Optional<GroupEntity> optionalGroupEntity;
        boolean conflictionGroupFlag = Boolean.FALSE;
        StringBuilder conflictingUserNames = new StringBuilder();
        for (UserDto memberData : groupData.getMembers()) {
            try {
                optionalGroupEntity = groupEntityRepository.findGroupByMemberContact(memberData.getContact(), setGroupName);
            } catch (Exception e) {
                log.error("DATABASE_ERR_IN_FETCHING_EXISTING_GROUP_INFO: {}", e.getMessage());
                throw new GenericRestException("DATABASE ERROR, PLEASE TRY LATER!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (optionalGroupEntity.isPresent()) {
                GroupEntity foundGroupEntity = optionalGroupEntity.get();
                if (foundGroupEntity.getName().equals(setGroupName)) {
                    conflictionGroupFlag = Boolean.TRUE;
                    conflictingUserNames.append(memberData.getName()).append(" ");
                }
            }
        }
        if (conflictionGroupFlag)
            throw new GenericRestException("ERR: SAME GROUP NAME ALREADY EXISTS FOR USERS: " + conflictingUserNames, HttpStatus.BAD_REQUEST);
        GroupEntity groupEntityData = getGroupEntity(groupData);
        GroupEntity savedGroupEntity;
        try {
            savedGroupEntity = groupEntityRepository.save(groupEntityData);
        } catch (Exception e) {
            log.error("DATABASE_ERR_IN_GROUP_DATA_CREATION: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE CREATING GROUP INFO IN DATABASE", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<UserModel> groupMembersList = getUserModels(savedGroupEntity);
        Map<String, Object> details = new HashMap<>();
        details.put("membersCount", groupMembersList.size());
        GroupModel createdGroupModel = new GroupModel(savedGroupEntity.getId(), savedGroupEntity.getName()
                , groupMembersList, groupData.getCreatedByContact());
        details.put("newGroupDetails", createdGroupModel);
        return new ResponseStatusDto("SUCCESS", "NEW GROUP SUCCESSFULLY CREATED!", details);
    }

    private GroupEntity getGroupEntity(GroupDto groupData) {
        GroupEntity groupEntityData = new GroupEntity();
        groupEntityData.setName(groupData.getName());
        List<UserEntity> membersDataList = new ArrayList<>();
        for (UserDto userData : groupData.getMembers()) {
            Optional<UserEntity> optionalUserEntity;
            try {
                optionalUserEntity = userEntityRepository.findByContact(userData.getContact());
            } catch (Exception e) {
                log.error("DATABASE ERROR IN FETCHING USER DETAILS: {}", e.getMessage());
                throw new GenericRestException("SERVER ERROR IN FETCHING USER DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (optionalUserEntity.isEmpty())
                throw new GenericRestException("USER DOES NOT EXIST: " + userData.getName(), HttpStatus.BAD_REQUEST);
            membersDataList.add(optionalUserEntity.get());
        }
        groupEntityData.setMembers(membersDataList);
        return groupEntityData;
    }

    public ResponseStatusDto getGroupMembers(BigInteger id) {
        Optional<GroupEntity> optionalGroupEntity;
        try {
            log.info("EXECUTING GROUP FETCH QUERY");
            optionalGroupEntity = groupEntityRepository.findById(id);
        } catch (Exception e) {
            log.error("ERR_WHILE_FETCHING_GROUP_DATA_FROM_DATABASE: {}", e.getMessage());
            throw new GenericRestException("DATABASE ERROR, UNABLE TO FETCH GROUP DATA", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalGroupEntity.isEmpty())
            throw new GenericRestException("ERROR: GROUP DOES NOT EXISTS!", HttpStatus.NOT_FOUND);
        Map<String, Object> details = new HashMap<>();
        List<UserModel> groupMembersList = getUserModels(optionalGroupEntity.get());
        details.put("membersCount", groupMembersList.size());
        details.put("groupMembers", groupMembersList);
        log.info("RETURNING RESPONSE");
        return new ResponseStatusDto("SUCCESS", "GROUP MEMBERS DETAILS FETCHED!", details);
    }

    private List<UserModel> getUserModels(GroupEntity groupEntity) {
        log.info("EXECUTING GET MEMBERS QUERY");
        List<UserEntity> fetcheUserEntitiesList = groupEntity.getMembers();
        List<UserModel> groupMembersList = new ArrayList<>();
        UserModel groupMember;
        UserEntity fetchedUserEntity;
        for (UserEntity userEntity : fetcheUserEntitiesList) {
            fetchedUserEntity = userEntity;
            groupMember = new UserModel();
            groupMember.setName(fetchedUserEntity.getName());
            groupMember.setContact(fetchedUserEntity.getContact());
            groupMember.setEmail(fetchedUserEntity.getEmail());
            groupMembersList.add(groupMember);
        }
        return groupMembersList;
    }

    public ResponseStatusDto addMember(UserDto memberData, BigInteger id) {
        Optional<GroupEntity> optionalGroupEntity;
        try {
            optionalGroupEntity = groupEntityRepository.findById(id);
        } catch (Exception e) {
            log.error("ERR_WHILE_FETCHING_GROUP_DATA_FROM_DATABASE: {}", e.getMessage());
            throw new GenericRestException("DATABASE ERROR, UNABLE TO FETCH GROUP DATA", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalGroupEntity.isEmpty())
            throw new GenericRestException("ERROR: GROUP DOES NOT EXISTS!", HttpStatus.BAD_REQUEST);
        Optional<UserEntity> optionalUser;
        try {
            optionalUser = userEntityRepository.findByContact(memberData.getContact());
        } catch (Exception e) {
            log.error("ERR_WHILE_FETCHING_USER_DATA_FROM_DATABASE: {}", e.getMessage());
            throw new GenericRestException("DATABASE ERROR, UNABLE TO FETCH USER DATA", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalUser.isEmpty())
            throw new GenericRestException("ERROR: ATTEMPTED USER DOES NOT EXIST!", HttpStatus.BAD_REQUEST);
        GroupEntity existingGroupEntity = optionalGroupEntity.get();
        UserEntity newMemberEntity = optionalUser.get();
        existingGroupEntity.getMembers().add(newMemberEntity);
        GroupEntity updatedGroupEntity;
        try {
            updatedGroupEntity = groupEntityRepository.save(existingGroupEntity);
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
        for (UserEntity updatedGroupUserEntity : updatedGroupEntity.getMembers()) {
            groupUserDto = new UserDto(updatedGroupUserEntity.getName()
                    , updatedGroupUserEntity.getContact(), updatedGroupUserEntity.getEmail());
            updatedGroupUserDtos.add(groupUserDto);
        }
        return Map.of("updatedGroupDetails", updatedGroupDto);
    }

    public ResponseStatusDto removeMember(UserDto memberData, BigInteger id) {
        Optional<GroupEntity> optionalGroupEntity;
        try {
            optionalGroupEntity = groupEntityRepository.findById(id);
        } catch (Exception e) {
            log.error("DATABASE_FETCH_ERR_FOR_GROUP_DATA: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE FETCHING GROUP DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalGroupEntity.isEmpty())
            throw new GenericRestException("GROUP DOES NOT EXIST", HttpStatus.BAD_REQUEST);
        GroupEntity foundGroupEntity = optionalGroupEntity.get();
        List<UserEntity> foundUserEntities = foundGroupEntity.getMembers();
        foundUserEntities.removeIf(memberEntity -> memberEntity.getContact().equals(memberData.getContact()));
        GroupEntity updatedGroupEntity;
        try {
            updatedGroupEntity = groupEntityRepository.save(foundGroupEntity);
        } catch (Exception e) {
            log.error("DATA_ERROR_WHILE_DELETING_MEMBER_IN_GROUP: {}", e.getMessage());
            throw new GenericRestException("SERVER ERROR WHILE DELETING MEMBER IN GROUP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Map<String, Object> details = getStringObjectMap(updatedGroupEntity);
        return new ResponseStatusDto("SUCCESS", "NEW MEMBER SUCCESSFULLY REMOVED FROM GROUP!", details);
    }

}
