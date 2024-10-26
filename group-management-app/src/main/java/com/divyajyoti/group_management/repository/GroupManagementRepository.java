package com.divyajyoti.group_management.repository;

import com.divyajyoti.group_management.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface GroupManagementRepository extends JpaRepository<GroupEntity, BigInteger> {

    //public List<UserDto> findAllMembers(BigInteger id);

    public Optional<GroupEntity> findByName(String name);

    @Query("SELECT g FROM GroupEntity g JOIN g.members u WHERE u.contact = :contact AND g.name = :groupName")
    public Optional<GroupEntity> findGroupByMemberContact(@Param("contact") String contact, @Param("groupName") String groupName);

}
