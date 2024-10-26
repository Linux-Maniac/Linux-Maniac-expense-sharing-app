package com.divyajyoti.user_management.repository;

import com.divyajyoti.user_management.entity.UserExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UserExpenseRepository extends JpaRepository<UserExpenseEntity, BigInteger> {
}
