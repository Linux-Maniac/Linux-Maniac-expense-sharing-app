package com.divyajyoti.expense_management.repositories;

import com.divyajyoti.expense_management.entities.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface GroupEntityRepository extends JpaRepository<GroupEntity, BigInteger> {
}
