package com.divyajyoti.expense_management.rests.controllers;

import com.divyajyoti.expense_management.dtos.ExpenseDto;
import com.divyajyoti.expense_management.dtos.ResponseStatusDto;
import com.divyajyoti.expense_management.services.ExpenseCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expense-management")
public class ExpenseManagementController {

    private final ExpenseCreationService expenseCreationService;

    @Autowired
    public ExpenseManagementController(ExpenseCreationService expenseCreationService){
        this.expenseCreationService = expenseCreationService;
    }

    @PostMapping("/new-expense")
    public ResponseEntity<ResponseStatusDto> addExpense(@RequestBody ExpenseDto expenseRequestDto){

        ResponseStatusDto responseStatusDto = expenseCreationService.addExpense(expenseRequestDto);
        return new ResponseEntity<>(responseStatusDto, HttpStatus.CREATED);
    }

}
