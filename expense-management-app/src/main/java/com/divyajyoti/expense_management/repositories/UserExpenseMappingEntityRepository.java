package com.divyajyoti.expense_management.repositories;

import com.divyajyoti.expense_management.entities.UserExpenseMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UserExpenseMappingEntityRepository extends JpaRepository<UserExpenseMappingEntity, BigInteger> {
}
