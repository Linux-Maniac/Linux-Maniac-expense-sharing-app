package com.divyajyoti.expense_management.rests.controllers;

import com.divyajyoti.expense_management.dtos.ExpenseDto;
import com.divyajyoti.expense_management.dtos.ResponseStatusDto;
import com.divyajyoti.expense_management.services.ExpenseOperationsService;
import com.divyajyoti.expense_management.services.ExpenseSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/expense-management")
public class ExpenseManagementController {

    private final ExpenseOperationsService expenseOperationsService;

    private final ExpenseSettlementService expenseSettlementService;

    @Autowired
    public ExpenseManagementController(ExpenseOperationsService expenseOperationsService
            , ExpenseSettlementService expenseSettlementService) {
        this.expenseOperationsService = expenseOperationsService;
        this.expenseSettlementService = expenseSettlementService;
    }

    @PostMapping("/new-expense")
    public ResponseEntity<ResponseStatusDto> addExpense(@RequestBody ExpenseDto expenseRequestDto) {
        ResponseStatusDto responseStatusDto = expenseOperationsService.addExpense(expenseRequestDto);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.CREATED);
    }

    @GetMapping("/settlement/group/{id}")
    public ResponseEntity<?> getSettlementInGroup(@PathVariable BigInteger id) {
        ResponseStatusDto responseStatusDto = expenseSettlementService.getSettlementInGroup(id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.OK);
    }

    @PutMapping("/settlement/group/settled/{id}")
    public ResponseEntity<?> setGroupExpensesToSettled(@PathVariable BigInteger id){
        ResponseStatusDto responseStatusDto = expenseOperationsService.setGroupExpensesToSettled(id);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.OK);
    }

}
