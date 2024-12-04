package com.divyajyoti.group_management.repositories;

import com.divyajyoti.group_management.entities.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface GroupEntityRepository extends JpaRepository<GroupEntity, BigInteger> {
}
