package com.divyajyoti.group_management.services;

import com.divyajyoti.group_management.dtos.GroupRequestDto;
import com.divyajyoti.group_management.dtos.GroupResponseDto;
import com.divyajyoti.group_management.dtos.ResponseStatusDto;
import com.divyajyoti.group_management.dtos.UserDto;
import com.divyajyoti.group_management.entities.GroupEntity;
import com.divyajyoti.group_management.entities.GroupMemberEntity;
import com.divyajyoti.group_management.models.GetUsersListFromUserServiceRespModel;
import com.divyajyoti.group_management.models.UserModel;
import com.divyajyoti.group_management.repositories.GroupEntityRepository;
import com.divyajyoti.group_management.repositories.GroupMemberEntityRepository;
import com.divyajyoti.group_management.rests.exceptions.GenericRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupManagementService {

    private final GroupEntityRepository groupEntityRepository;

    private final GroupMemberEntityRepository groupMemberEntityRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public GroupManagementService(GroupEntityRepository groupEntityRepository,
                                  RestTemplate restTemplate,
                                  GroupMemberEntityRepository groupMemberEntityRepository) {
        this.groupEntityRepository = groupEntityRepository;
        this.restTemplate = restTemplate;
        this.groupMemberEntityRepository = groupMemberEntityRepository;
    }

    public ResponseStatusDto createGroup(GroupRequestDto groupData) {
        GroupEntity savedGroupEntity = saveGroupEntity(groupData.getName());
        List<UserModel> userModelList = getUsersList(groupData);

        List<GroupMemberEntity> groupMembers = userModelList.stream()
                .map(user -> {
                    GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
                    groupMemberEntity.setGroup(savedGroupEntity);
                    groupMemberEntity.setUserId(user.getId());
                    return groupMemberEntity;
                })
                .collect(Collectors.toList());

        saveGroupMembers(groupMembers);

        Map<String, Object> details = buildGroupResponse(groupData, userModelList, savedGroupEntity);
        return new ResponseStatusDto("SUCCESS", "NEW GROUP SUCCESSFULLY CREATED!", details);
    }

    private GroupEntity saveGroupEntity(String groupName) {
        try {
            GroupEntity groupEntity = new GroupEntity();
            groupEntity.setName(groupName);
            return groupEntityRepository.save(groupEntity);
        } catch (Exception e) {
            log.error("DATABASE_ERR_IN_GROUP_DATA_CREATION: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE CREATING GROUP INFO IN DATABASE", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveGroupMembers(List<GroupMemberEntity> groupMembers) {
        try {
            groupMemberEntityRepository.saveAll(groupMembers);
        } catch (Exception e) {
            log.error("DATABASE_ERR_IN_GROUP__MEMBER_DATA_CREATION: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE CREATING GROUP MEMBERS INFO IN DATABASE", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> buildGroupResponse(GroupRequestDto groupData,
                                                   List<UserModel> userModelList,
                                                   GroupEntity savedGroupEntity) {
        List<UserDto> groupMembersList = userModelList.stream().map(user -> {
            UserDto userDto = new UserDto();
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            userDto.setContact(user.getContact());
            return userDto;
        }).collect(Collectors.toList());

        Map<String, Object> details = new HashMap<>();
        details.put("membersCount", groupMembersList.size());
        GroupResponseDto groupResponseDto = new GroupResponseDto(savedGroupEntity.getId(), savedGroupEntity.getName(), groupMembersList, groupData.getCreatedByContact());
        details.put("newGroupDetails", groupResponseDto);
        return details;
    }

    // Generalized method for making any REST call
    private <T> ResponseEntity<?> makeRestCall(String url, HttpMethod method, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<?> response;
        try {
            // Attempt to make the REST call
            response = restTemplate.exchange(url, method, entity, responseType);
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle 4xx or 5xx status codes (Client or Server Errors)
            String errorMessage = String.format("HTTP Error: %s - %s for URL: %s. Response: %s", e.getStatusCode(), e.getMessage(), url, e.getResponseBodyAsString());
            log.error(errorMessage, e);
            response = new ResponseEntity<>(errorMessage, (HttpStatus) e.getStatusCode());
            return response;
        } catch (ResourceAccessException e) {
            // Handle network issues, timeouts, etc.
            String errorMessage = String.format("Network error or timeout occurred while calling URL: %s. Error: %s", url, e.getMessage());
            log.error(errorMessage, e);
            throw new GenericRestException("NETWORK ERROR OR TIMEOUT OCCURRED", HttpStatus.GATEWAY_TIMEOUT);
        } catch (Exception e) {
            // Catch any other general exceptions
            String errorMessage = String.format("An unexpected error occurred while making REST call to URL: %s. Error: %s", url, e.getMessage());
            log.error(errorMessage, e);
            throw new GenericRestException("UNEXPECTED ERROR OCCURRED", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseStatusDto getGroupMembers(BigInteger id) {
        GroupEntity groupEntity = getGroupEntity(id);

        List<UserDto> groupMemberDtosList = getUserModels(groupEntity).stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());

        Map<String, Object> details = new HashMap<>();
        details.put("membersCount", groupMemberDtosList.size());
        details.put("groupMembers", groupMemberDtosList);
        return new ResponseStatusDto("SUCCESS", "GROUP MEMBERS DETAILS FETCHED!", details);
    }

    private GroupEntity getGroupEntity(BigInteger id) {
        return groupEntityRepository.findById(id).orElseThrow(() -> {
            log.error("GROUP NOT FOUND: {}", id);
            return new GenericRestException("GROUP DOES NOT EXISTS!", HttpStatus.NOT_FOUND);
        });
    }

    private UserDto mapToUserDto(UserModel userModel) {
        return new UserDto(userModel.getName(), userModel.getEmail(), userModel.getContact(),
                userModel.getFirstName(), userModel.getLastName());
    }

    private List<UserModel> getUserModels(GroupEntity groupEntity) {
        List<BigInteger> userIdsList = groupEntity.getGroupMemberEntityList().stream()
                .map(GroupMemberEntity::getUserId)
                .collect(Collectors.toList());

        // Use the generalized method for any REST call (by ID)
        ResponseEntity<?> response = makeRestCall("http://USER-MANAGEMENT-APP/user-management/user-details-list/by-id",
                HttpMethod.POST, userIdsList, GetUsersListFromUserServiceRespModel.class);

        if (response.getStatusCode().is5xxServerError())
            throw new GenericRestException("SERVER ERROR IN FETCHING USER DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);

        ResponseEntity<GetUsersListFromUserServiceRespModel> typedResponse =
                (ResponseEntity<GetUsersListFromUserServiceRespModel>) response;

        if (typedResponse.getBody().getDetails().getUsersList().isEmpty())
            throw new GenericRestException("USERS DO NOT EXISTS", HttpStatus.BAD_REQUEST);

        return typedResponse.getBody().getDetails().getUsersList();
    }

    // Refactored getUsersList method
    private List<UserModel> getUsersList(GroupRequestDto groupData) {
        List<String> contactsList = groupData.getMembers().stream()
                .map(UserDto::getContact)
                .collect(Collectors.toList());

        // Use the generalized method for any REST call (by contact)
        ResponseEntity<?> response = makeRestCall("http://USER-MANAGEMENT-APP/user-management/user-details-list/by-contact",
                HttpMethod.POST, contactsList, GetUsersListFromUserServiceRespModel.class);

        if (response.getStatusCode().is5xxServerError())
            throw new GenericRestException("SERVER ERROR IN FETCHING USER DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);

        ResponseEntity<GetUsersListFromUserServiceRespModel> typedResponse =
                (ResponseEntity<GetUsersListFromUserServiceRespModel>) response;

        if (typedResponse.getBody().getDetails().getUsersList().isEmpty())
            throw new GenericRestException("USERS DO NO EXIST", HttpStatus.BAD_REQUEST);

        List<UserModel> userList = typedResponse.getBody().getDetails().getUsersList();

        Set<String> fetchedUserContactsSet = userList.stream()
                .map(UserModel::getContact)
                .collect(Collectors.toSet());

        // Validate users exist in the fetched data
        validateUsersExist(groupData.getMembers(), fetchedUserContactsSet);

        return userList;
    }

    private void validateUsersExist(List<UserDto> userDtos, Set<String> fetchedUserContactsSet) {
        for (UserDto userDto : userDtos) {
            if (!fetchedUserContactsSet.contains(userDto.getContact())) {
                throw new GenericRestException("USER DOES NOT EXIST: " + userDto.getName(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public ResponseStatusDto addMember(UserDto memberData, BigInteger groupId) {
        GroupEntity groupEntity = getGroupEntity(groupId);
        UserModel user = getUserByContact(memberData.getContact());

        addMemberToGroup(groupEntity, user);

        return new ResponseStatusDto("SUCCESS", "NEW MEMBER SUCCESSFULLY ADDED TO GROUP!", null);
    }

    private UserModel getUserByContact(String contact) {
        List<String> contactList = Collections.singletonList(contact);

        // Use the generalized method for any REST call (by contact)
        ResponseEntity<?> response = makeRestCall("http://USER-MANAGEMENT-APP/user-management/user-details-list/by-contact",
                HttpMethod.POST, contactList, GetUsersListFromUserServiceRespModel.class);

        if (response.getStatusCode().is5xxServerError())
            throw new GenericRestException("SERVER ERROR IN FETCHING USER DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);

        ResponseEntity<GetUsersListFromUserServiceRespModel> typedResponse =
                (ResponseEntity<GetUsersListFromUserServiceRespModel>) response;

        List<UserModel> userList = typedResponse.getBody().getDetails().getUsersList();

        if (userList.isEmpty())
            throw new GenericRestException("USER DOES NOT EXISTS", HttpStatus.BAD_REQUEST);

        return userList.get(0);
    }

    private void addMemberToGroup(GroupEntity groupEntity, UserModel user) {
        GroupMemberEntity newGroupMemberEntity = new GroupMemberEntity();
        newGroupMemberEntity.setGroup(groupEntity);
        newGroupMemberEntity.setUserId(user.getId());
        groupEntity.getGroupMemberEntityList().add(newGroupMemberEntity);
        try {
            groupEntityRepository.save(groupEntity);
        } catch (Exception e) {
            log.error("ERROR IN ADDING NEW GROUP MEMBER: {}", e.getMessage());
            throw new GenericRestException("ERROR WHILE ADDING NEW MEMBER TO GROUP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseStatusDto removeMember(UserDto memberData, BigInteger groupId) {
        // Step 1: Retrieve the group
        GroupEntity groupEntity = getGroupEntity(groupId);

        // Step 2: Find the user in the group by contact
        UserModel user = getUserByContact(memberData.getContact());

        // Step 3: Remove the user from the group
        removeMemberFromGroup(groupEntity, user);

        // Step 4: Return a success response
        return new ResponseStatusDto("SUCCESS", "MEMBER SUCCESSFULLY REMOVED FROM GROUP!", null);
    }

    private void removeMemberFromGroup(GroupEntity groupEntity, UserModel user) {
        // Step 1: Find the GroupMemberEntity for the given user and group
        Optional<GroupMemberEntity> groupMemberEntityOpt = groupEntity.getGroupMemberEntityList().stream()
                .filter(member -> member.getUserId().equals(user.getId()))
                .findFirst();

        if (groupMemberEntityOpt.isPresent()) {
            // Step 2: If the member exists, remove them from the group
            GroupMemberEntity groupMemberEntity = groupMemberEntityOpt.get();
            groupEntity.getGroupMemberEntityList().remove(groupMemberEntity);

            try {
                // Step 3: Save the updated group entity after removal
                groupEntityRepository.save(groupEntity);
            } catch (Exception e) {
                log.error("ERROR IN REMOVING GROUP MEMBER: {}", e.getMessage());
                throw new GenericRestException("ERROR WHILE REMOVING MEMBER FROM GROUP", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // Step 4: If the user is not part of the group, throw an exception
            log.error("USER NOT FOUND IN GROUP: {} for group ID: {}", user.getContact(), groupEntity.getId());
            throw new GenericRestException("USER NOT A MEMBER OF THE GROUP", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseStatusDto getGroupDetails(BigInteger id) {

        try {

        } catch (Exception e) {
        }
        return null;
    }

}
