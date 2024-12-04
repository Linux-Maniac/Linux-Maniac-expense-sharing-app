package com.divyajyoti.group_management.repositories;

import com.divyajyoti.group_management.entities.GroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Repository
public interface GroupMemberEntityRepository extends JpaRepository<GroupMemberEntity, BigInteger> {

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupMemberEntity g WHERE g.id = :id")
    public void deleteById1(@Param("id") BigInteger id);

}
