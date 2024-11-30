package com.divyajyoti.expense_management.repositories;

import com.divyajyoti.expense_management.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseEntityRepository extends JpaRepository<ExpenseEntity, BigInteger> {

    @Query("SELECT e FROM ExpenseEntity e WHERE e.groupEntity.id = :groupId AND e.isSettled = 'FALSE'")
    public List<ExpenseEntity> findNonSettledExpensesByGroupEntity_Id(BigInteger groupId);

}
