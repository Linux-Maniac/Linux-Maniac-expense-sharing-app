package com.divyajyoti.expense_management.services;

import com.divyajyoti.expense_management.constants.ExpenseType;
import com.divyajyoti.expense_management.dtos.ResponseStatusDto;
import com.divyajyoti.expense_management.entities.ExpenseEntity;
import com.divyajyoti.expense_management.entities.UserExpenseMappingEntity;
import com.divyajyoti.expense_management.models.GetUsersListFromUserServiceRespModel;
import com.divyajyoti.expense_management.models.GroupModel;
import com.divyajyoti.expense_management.models.UserModel;
import com.divyajyoti.expense_management.models.UserShareMappingModel;
import com.divyajyoti.expense_management.repositories.ExpenseEntityRepository;
import com.divyajyoti.expense_management.repositories.UserExpenseMappingEntityRepository;
import com.divyajyoti.expense_management.rests.exceptions.GenericRestException;
import com.divyajyoti.expense_management.utilities.CommonServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
public class ExpenseSettlementService {

    private final ExpenseEntityRepository expenseEntityRepository;

    private final UserExpenseMappingEntityRepository userExpenseMappingEntityRepository;

    private final CommonServices commonServices;

    @Autowired
    public ExpenseSettlementService(ExpenseEntityRepository expenseEntityRepository
            , UserExpenseMappingEntityRepository userExpenseMappingEntityRepository
            ,CommonServices commonServices) {
        this.expenseEntityRepository = expenseEntityRepository;
        this.userExpenseMappingEntityRepository = userExpenseMappingEntityRepository;
        this.commonServices = commonServices;
    }

    public ResponseStatusDto getSettlementInGroup(BigInteger groupId) {
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

        responseStatusDto = new ResponseStatusDto();

        List<UserModel> userModelList = new ArrayList<>();
        Map<String, Double> userContactsBalancesMap = new HashMap<>();

        // Process expenses and update user balances
        for (ExpenseEntity expenseEntity : expenseEntityList) {

            ResponseEntity<?> response = commonServices.makeRestCall("http://localhost:8081/user-management/group-details/{id}",
                    HttpMethod.GET, expenseEntity.getPaidByUserId(), UserModel.class);

            if(response.getStatusCode().is4xxClientError())
                throw new GenericRestException("DATA ERROR", HttpStatus.BAD_REQUEST);

            @SuppressWarnings("unchecked")
            ResponseEntity<UserModel> typedResponse = (ResponseEntity<UserModel>) response;

            UserModel paidByUserModel = typedResponse.getBody();
            String paidByContact = paidByUserModel.getContact();

            // Ensure user is added only once to the userModelList
            if (!userContactsBalancesMap.containsKey(paidByContact)) {
                userModelList.add(paidByUserModel);
            }

            // Update balance for the user who paid
            userContactsBalancesMap.merge(paidByContact, expenseEntity.getTotalAmount(), Double::sum);

            // Process individual user share from expense mappings
            for (UserExpenseMappingEntity userExpenseMappingEntity : expenseEntity.getUserExpenseMappingEntityList()) {
                if (userExpenseMappingEntity.getExpenseType() == ExpenseType.SELF_PAID)
                    continue;

                ResponseEntity<?> getPayeeResponse = commonServices.makeRestCall("http://localhost:8081/user-management/group-details/{id}",
                        HttpMethod.GET, userExpenseMappingEntity.getUserId(), UserModel.class);

                if(response.getStatusCode().is4xxClientError())
                    throw new GenericRestException("DATA ERROR", HttpStatus.BAD_REQUEST);

                @SuppressWarnings("unchecked")
                ResponseEntity<UserModel> getPayeeTypedResponse = (ResponseEntity<UserModel>) getPayeeResponse;

                UserModel payeeUserModel = getPayeeTypedResponse.getBody();
                String payeeContact = payeeUserModel.getContact();

                // Ensure user is added only once to the userModelList
                if (!userContactsBalancesMap.containsKey(payeeContact)) {
                    userModelList.add(payeeUserModel);
                }

                // Update balance for the user who should pay
                userContactsBalancesMap.merge(payeeContact, -userExpenseMappingEntity.getAmount(), Double::sum);

            }
        }

        // Prepare the models for balancing
        PriorityQueue<UserShareMappingModel> payeeMaxHeap = new PriorityQueue<>((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
        PriorityQueue<UserShareMappingModel> payerMinHeap = new PriorityQueue<>((a, b) -> Double.compare(a.getAmount(), b.getAmount()));

        for (UserModel userModel : userModelList) {
            double shareAmount = userContactsBalancesMap.get(userModel.getContact());
            UserShareMappingModel userShareMappingModel = new UserShareMappingModel(userModel, shareAmount);

            // Classify users based on their balance
            if (shareAmount > 0) {
                payeeMaxHeap.offer(userShareMappingModel);
            } else {
                payerMinHeap.offer(userShareMappingModel);
            }
        }

        List<String> paymentsMessageList = new ArrayList<>();

        // Settle payments between debtors and creditors
        while (!payeeMaxHeap.isEmpty() && !payerMinHeap.isEmpty()) {
            UserShareMappingModel debtor = payerMinHeap.poll();
            UserShareMappingModel creditor = payeeMaxHeap.poll();

            double amount = Math.min(-debtor.getAmount(), creditor.getAmount());
            String message = debtor.getUser().getName() + " pays " + amount + " to " + creditor.getUser().getName();
            paymentsMessageList.add(message);

            // Update the balances for both debtor and creditor
            debtor.setAmount(debtor.getAmount() + amount);
            creditor.setAmount(creditor.getAmount() - amount);

            // Re-add users to the heap if they still have remaining balances
            if (debtor.getAmount() < 0) {
                payerMinHeap.offer(debtor);
            }

            if (creditor.getAmount() > 0) {
                payeeMaxHeap.offer(creditor);
            }
        }

        responseStatusDto.setStatus("SUCCESS");
        responseStatusDto.setDetails(paymentsMessageList);
        return responseStatusDto;
    }

}
