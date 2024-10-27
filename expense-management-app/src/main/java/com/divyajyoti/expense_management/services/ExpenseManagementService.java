package com.divyajyoti.expense_management.services;

import com.divyajyoti.expense_management.constants.ExpenseType;
import com.divyajyoti.expense_management.constants.SplitType;
import com.divyajyoti.expense_management.dtos.ExpenseDto;
import com.divyajyoti.expense_management.dtos.ResponseStatusDto;
import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.entities.ExpenseEntity;
import com.divyajyoti.expense_management.entities.GroupEntity;
import com.divyajyoti.expense_management.entities.UserEntity;
import com.divyajyoti.expense_management.entities.UserExpenseMappingEntity;
import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;
import com.divyajyoti.expense_management.models.expense.extends_classes.ExactExpenseModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.ExactSplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.GenericSplitModel;
import com.divyajyoti.expense_management.repositories.ExpenseEntityRepository;
import com.divyajyoti.expense_management.repositories.GroupEntityRepository;
import com.divyajyoti.expense_management.repositories.UserEntityRepository;
import com.divyajyoti.expense_management.rests.exceptions.GenericRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExpenseManagementService {

    private final ExpenseEntityRepository expenseEntityRepository;

    private final UserEntityRepository userEntityRepository;

    private final GroupEntityRepository groupEntityRepository;

    @Autowired
    public ExpenseManagementService(ExpenseEntityRepository expenseEntityRepository
            , UserEntityRepository userEntityRepository, GroupEntityRepository groupEntityRepository) {
        this.expenseEntityRepository = expenseEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.groupEntityRepository = groupEntityRepository;
    }

    public ResponseStatusDto addExpense(ExpenseDto expenseRequestDto) {
        SplitType expenseType = expenseRequestDto.getSplitType();
        double totalAmount = expenseRequestDto.getTotalAmount();
        UserDto paidBy = expenseRequestDto.getPaidBy();
        String name = expenseRequestDto.getDescription();
        List<GenericSplitModel> requestUserSplitsList = expenseRequestDto.getUserSplitsList();
        List<SplitModel> splitModelList = new ArrayList<>();
        switch (expenseType) {
            case EXACT:
                log.info("EXECUTING EXACT SPLIT_TYPE FOR EXPENSE_NAME: {}", expenseRequestDto.getDescription());
                for (GenericSplitModel genericSplitModel : requestUserSplitsList) {
                    splitModelList.add(new ExactSplitModel(genericSplitModel.getUser(), genericSplitModel.getAmount()));
                }
                ExpenseModel expenseModel = new ExactExpenseModel(name, totalAmount, paidBy, splitModelList);
                if (!expenseModel.isValid())
                    throw new GenericRestException("INCORRECT SPLITTED AMOUNTS AND TOTAL AMOUNT", HttpStatus.BAD_REQUEST);
                Optional<GroupEntity> optionalGroupEntity;
                try {
                    optionalGroupEntity = groupEntityRepository.findById(expenseRequestDto.getGroupId());
                } catch (Exception e) {
                    log.error("DATABASE ERROR IN FETCHING GROUP DETAILS: {}", e.getMessage());
                    throw new GenericRestException("SERVER ERROR IN FETCHING GROUP DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (optionalGroupEntity.isEmpty())
                    throw new GenericRestException("GROUP PROVIDED DOES NOT EXIST", HttpStatus.BAD_REQUEST);
                List<UserExpenseMappingEntity> userExpenseMappingEntityList = new ArrayList<>();
                ExpenseEntity providedExpenseEntity = new ExpenseEntity();
                providedExpenseEntity.setDescription(expenseRequestDto.getDescription());
                providedExpenseEntity.setTotalAmount(expenseRequestDto.getTotalAmount());
                providedExpenseEntity.setUserExpenseMappingEntityList(userExpenseMappingEntityList);
                UserExpenseMappingEntity userExpenseMappingEntity;
                for (SplitModel split : expenseRequestDto.getUserSplitsList()) {
                    Optional<UserEntity> optionalUserEntity;
                    try{
                        optionalUserEntity = userEntityRepository.findByContact(split.getUser().getContact());
                    } catch (Exception e){
                        log.error("ERR WHILE FETCHING USER DATA FROM DATABASE: {}", e.getMessage());
                        throw new GenericRestException("SERVER ERROR WHILE FETCHING USER DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    if(optionalUserEntity.isEmpty())
                        throw new GenericRestException("USER PROVIDED DOES NOT EXIST: " + split.getUser().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
                    UserEntity fetchedUserEntity = optionalUserEntity.get();
                    userExpenseMappingEntity = new UserExpenseMappingEntity();
                    userExpenseMappingEntity.setExpenseEntity(providedExpenseEntity);
                    userExpenseMappingEntity.setUserEntity(fetchedUserEntity);
                    userExpenseMappingEntity.setAmount(split.getAmount());
                    if(fetchedUserEntity.getContact().equals(paidBy.getContact()))
                        userExpenseMappingEntity.setExpenseType(ExpenseType.PAID_BY);
                    else
                        userExpenseMappingEntity.setExpenseType(ExpenseType.OWES_IT);
                    userExpenseMappingEntityList.add(userExpenseMappingEntity);
                }
                ExpenseEntity savedExpenseEntity;
                try{
                    savedExpenseEntity = expenseEntityRepository.save(providedExpenseEntity);
                } catch (Exception e){
                    log.error("ERROR WHILE SAVING EXPENSE DETAILS INTO DATABASE: {}", e.getMessage());
                    throw new GenericRestException("SERVER ERROR WHILE SAVING EXPENSE DETAILS", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            /*case PERCENT:
                for (Split split : splits) {
                    PercentSplit percentSplit = (PercentSplit) split;
                    split.setAmount((amount * percentSplit.getPercent()) / 100.0);
                }
                return new PercentExpense(amount, paidBy, splits, expenseMetadata);
            case EQUAL:
                int totalSplits = splits.size();
                double splitAmount = ((double) Math.round(amount * 100 / totalSplits)) / 100.0;
                for (Split split : splits) {
                    split.setAmount(splitAmount);
                }
                splits.get(0).setAmount(splitAmount + (amount - splitAmount * totalSplits));
                return new EqualExpense(amount, paidBy, splits, expenseMetadata);*/
            default:
                return null;
        }
        return new ResponseStatusDto("SUCCESS", null);
    }
}
