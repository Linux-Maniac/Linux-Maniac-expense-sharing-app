package com.divyajyoti.group_management.repository;

import com.divyajyoti.group_management.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserEntity, BigInteger> {

    Optional<UserEntity> findByContact(String contact);

}
