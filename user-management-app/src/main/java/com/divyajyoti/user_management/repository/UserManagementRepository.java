package com.divyajyoti.user_management.repository;

import com.divyajyoti.user_management.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserManagementRepository extends JpaRepository<UserEntity, BigInteger> {

    public Optional<UserEntity> findByContact(String contact);
}
