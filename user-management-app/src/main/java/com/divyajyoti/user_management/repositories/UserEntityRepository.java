package com.divyajyoti.user_management.repositories;

import com.divyajyoti.user_management.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, BigInteger> {

    public Optional<UserEntity> findByContact(String contact);

    @Query("SELECT u FROM UserEntity u WHERE u.contact IN :contactsList")
    public List<UserEntity> findUsersListByContacts(@Param("contactsList") List<String> contactsList);

    @Query("SELECT u FROM UserEntity u WHERE u.id IN :idsList")
    public List<UserEntity> findUsersListByIds(@Param("idsList") List<String> idsList);

}
