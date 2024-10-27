package com.divyajyoti.expense_management.repositories;

import com.divyajyoti.expense_management.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, BigInteger> {

    public Optional<UserEntity> findByContact(String contact);

}
