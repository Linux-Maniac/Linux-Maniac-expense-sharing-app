package com.divyajyoti.expense_management.services;

import com.divyajyoti.expense_management.constants.ExpenseType;
import com.divyajyoti.expense_management.constants.SplitType;
import com.divyajyoti.expense_management.dtos.ExpenseDto;
import com.divyajyoti.expense_management.dtos.ResponseStatusDto;
import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.entities.ExpenseEntity;
import com.divyajyoti.expense_management.entities.UserExpenseMappingEntity;
import com.divyajyoti.expense_management.models.GetUsersListFromUserServiceRespModel;
import com.divyajyoti.expense_management.models.GroupModel;
import com.divyajyoti.expense_management.models.UserModel;
import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;
import com.divyajyoti.expense_management.models.expense.extends_classes.EqualExpenseModel;
import com.divyajyoti.expense_management.models.expense.extends_classes.ExactExpenseModel;
import com.divyajyoti.expense_management.models.expense.extends_classes.PercentExpenseModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.EqualSplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.ExactSplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.GenericSplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.PercentSplitModel;
import com.divyajyoti.expense_management.repositories.ExpenseEntityRepository;
import com.divyajyoti.expense_management.rests.exceptions.GenericRestException;
import com.divyajyoti.expense_management.utilities.CommonServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ExpenseOperationsService {

    private final ExpenseEntityRepository expenseEntityRepository;

    private final CommonServices commonServices;

    @Autowired
    public ExpenseOperationsService(ExpenseEntityRepository expenseEntityRepository
            , CommonServices commonServices) {
        this.expenseEntityRepository = expenseEntityRepository;
        this.commonServices = commonServices;
    }

    public ResponseStatusDto addExpense(ExpenseDto expenseRequestDto) {
        UserModel paidByUserModel = createUserModel(expenseRequestDto.getPaidBy());
        String description = expenseRequestDto.getDescription();
        List<GenericSplitModel> requestUserSplitsList = expenseRequestDto.getUserSplitsList();

        ExpenseModel expenseModel;
        SplitType expenseType = expenseRequestDto.getSplitType();
        double totalAmount = expenseRequestDto.getTotalAmount();

        expenseModel = switch (expenseType) {
            case EXACT -> createExactExpenseModel(requestUserSplitsList, description, totalAmount, paidByUserModel);
            case PERCENT -> createPercentExpenseModel(requestUserSplitsList, description, totalAmount, paidByUserModel);
            case EQUAL -> createEqualExpenseModel(requestUserSplitsList, description, totalAmount, paidByUserModel);
            default -> throw new GenericRestException("INVALID SPLIT TYPE!", HttpStatus.BAD_REQUEST);
        };

        expenseModel.setIsSettled(Boolean.FALSE);
        saveExpense(expenseRequestDto, expenseModel, paidByUserModel);
        return new ResponseStatusDto("SUCCESS", Collections.singletonMap("expenseDetails", expenseModel));
    }

    private UserModel createUserModel(UserDto userDto) {
        UserModel userModel = new UserModel();
        userModel.setName(userDto.getName());
        userModel.setContact(userDto.getContact());
        userModel.setEmail(userDto.getEmail());
        return userModel;
    }

    private ExpenseModel createExactExpenseModel(List<GenericSplitModel> requestUserSplitsList, String description, double totalAmount, UserModel paidByUserModel) {
        List<SplitModel> splitModelList = new ArrayList<>();
        for (GenericSplitModel split : requestUserSplitsList) {
            splitModelList.add(new ExactSplitModel(split.getUser(), split.getAmount()));
        }
        ExactExpenseModel expenseModel = new ExactExpenseModel(description, totalAmount, paidByUserModel, splitModelList);
        if (!expenseModel.isValid())
            throw new GenericRestException("MISMATCH BETWEEN SUM OF EXACT SPLITS AND TOTAL AMOUNT", HttpStatus.BAD_REQUEST);
        return expenseModel;
    }

    private ExpenseModel createPercentExpenseModel(List<GenericSplitModel> requestUserSplitsList, String description, double totalAmount, UserModel paidByUserModel) {
        List<SplitModel> splitModelList = new ArrayList<>();
        for (GenericSplitModel split : requestUserSplitsList) {
            splitModelList.add(new PercentSplitModel(split.getUser(), split.getAmount()));
        }

        PercentExpenseModel expenseModel = new PercentExpenseModel(description, totalAmount, paidByUserModel, splitModelList);
        if (!expenseModel.isValid())
            throw new GenericRestException("SUM OF SPLIT PERCENTS EXCEEDS 100%", HttpStatus.BAD_REQUEST);
        for (SplitModel splitModel : expenseModel.getSplitDetails()) {
            PercentSplitModel percentSplitModel = (PercentSplitModel) splitModel;
            percentSplitModel.setAmount((totalAmount * percentSplitModel.getPercent()) / 100);
        }
        return expenseModel;
    }

    private ExpenseModel createEqualExpenseModel(List<GenericSplitModel> requestUserSplitsList, String description, double totalAmount, UserModel paidByUserModel) {
        List<SplitModel> splitModelList = new ArrayList<>();
        for (GenericSplitModel split : requestUserSplitsList) {
            splitModelList.add(new EqualSplitModel(split.getUser()));
        }
        double splitAmount = totalAmount / splitModelList.size();
        for (SplitModel split : splitModelList) {
            split.setAmount(splitAmount);
        }
        return new EqualExpenseModel(description, totalAmount, paidByUserModel, splitModelList);
    }

    private void saveExpense(ExpenseDto expenseRequestDto, ExpenseModel expenseModel, UserModel paidByUserModel) {

        BigInteger groupId = null;

        // Fetch the group entity if a group ID is provided
        if (expenseRequestDto.getGroupId() != null) {
            ResponseEntity<?> response = commonServices.makeRestCall("http://localhost:8081/group-management/group-details/{id}",
                    HttpMethod.POST, expenseRequestDto.getGroupId(), GroupModel.class);

            if (response.getStatusCode().is4xxClientError())
                throw new GenericRestException("GROUP DOES NOT EXISTS", HttpStatus.BAD_REQUEST);

            @SuppressWarnings("unchecked")
            ResponseEntity<GroupModel> typedResponse = (ResponseEntity<GroupModel>) response;


            GroupModel groupDetails = typedResponse.getBody();
            groupId = groupDetails.getId();

            // If a group is associated, set the group details in the expense model
            expenseModel.setGroup(groupDetails);

        }

        // Prepare the ExpenseEntity
        ExpenseEntity providedExpenseEntity = new ExpenseEntity();
        providedExpenseEntity.setDescription(expenseModel.getDescription());
        providedExpenseEntity.setTotalAmount(expenseModel.getTotalAmount());
        providedExpenseEntity.setIsSettledFromBoolean(expenseModel.getIsSettled());
        providedExpenseEntity.setGroupId(groupId);

        List<UserExpenseMappingEntity> userExpenseMappingEntityList = new ArrayList<>();

        for (SplitModel split : expenseModel.getSplitDetails()) {
            ResponseEntity<?> response = commonServices.makeRestCall("http://localhost:8081/user-management/group-details/{id}",
                    HttpMethod.POST, split.getUser().getContact(), GetUsersListFromUserServiceRespModel.class);

            if(response.getStatusCode().value() == 404)
                throw new GenericRestException("USER PROVIDED DOES NOT EXIST: " + split.getUser().getName()
                        , HttpStatus.BAD_REQUEST);

            @SuppressWarnings("unchecked")
            ResponseEntity<GetUsersListFromUserServiceRespModel> typedResponse = (ResponseEntity<GetUsersListFromUserServiceRespModel>) response;

            UserModel fetchedUserModel = typedResponse.getBody().getDetails().getUsersList().getFirst();

            UserExpenseMappingEntity userExpenseMappingEntity = new UserExpenseMappingEntity();
            if (fetchedUserModel.getContact().equals(paidByUserModel.getContact())) {
                providedExpenseEntity.setPaidByUserId(fetchedUserModel.getId());
                userExpenseMappingEntity.setExpenseType(ExpenseType.SELF_PAID);
                userExpenseMappingEntity.setAmount(expenseModel.getTotalAmount());
                paidByUserModel.setId(fetchedUserModel.getId());
            } else {
                userExpenseMappingEntity.setExpenseType(ExpenseType.OWES_IT);
                userExpenseMappingEntity.setAmount(split.getAmount());
            }

            userExpenseMappingEntity.setExpenseEntity(providedExpenseEntity);
            userExpenseMappingEntity.setUserId(fetchedUserModel.getId());

            userExpenseMappingEntityList.add(userExpenseMappingEntity);
        }

        providedExpenseEntity.setUserExpenseMappingEntityList(userExpenseMappingEntityList);

        // Save the ExpenseEntity
        try {
            ExpenseEntity savedExpenseEntity = expenseEntityRepository.save(providedExpenseEntity);

            // Set the ID of the saved expense model
            expenseModel.setId(savedExpenseEntity.getId());

        } catch (Exception e) {
            log.error("ERROR WHILE SAVING EXPENSE DETAILS INTO DATABASE: {}", e.getMessage());
            throw new GenericRestException("SERVER ERROR WHILE SAVING EXPENSE DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseStatusDto setGroupExpensesToSettled(BigInteger groupId) {
        List<ExpenseEntity> expenseEntityList;
        ResponseStatusDto responseStatusDto = null;

        try {
            log.info("FETCHING EXPENSE IN GROUP ID: {}", groupId);
            expenseEntityList = expenseEntityRepository.findNonSettledExpensesByGroupEntity_Id(groupId);
            log.info("FETCHED TOTAL EXPENSES NO: {}", expenseEntityList.size());
        } catch (Exception e) {
            log.error("DATABASE ERROR WHILE FETCHING EXPENSE DETAILS: {}", e.getMessage());
            throw new GenericRestException("SERVER ERROR WHILE FETCHING EXPENSE DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (expenseEntityList.isEmpty()) {
            throw new GenericRestException("CANNOT PERFORM SETTLEMENT IN GROUP AS CURRENTLY NO UNSETTLED EXPENSES IN GROUP", HttpStatus.CONFLICT);

        }

        for (ExpenseEntity expense : expenseEntityList)
            expense.setIsSettled("TRUE");
        try {
            expenseEntityRepository.saveAll(expenseEntityList);
        } catch (Exception e) {
            log.error("DATABASE ERROR WHILE SETTING GROUP EXPENSES TO SETTLED: {}", e.getMessage());
            throw new GenericRestException("SERVER ERROR WHILE SETTING GROUP EXPENSES TO SETTLED", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseStatusDto = new ResponseStatusDto();
        responseStatusDto.setStatus("SUCCESS");
        responseStatusDto.setDetails("ALL GROUP EXPENSES HAVE BEEN SET TO SETTLED");
        return responseStatusDto;
    }

}
