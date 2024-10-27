package com.divyajyoti.group_management.repositories;

import com.divyajyoti.group_management.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, BigInteger> {

    Optional<UserEntity> findByContact(String contact);

}
