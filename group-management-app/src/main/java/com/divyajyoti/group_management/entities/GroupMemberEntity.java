package com.divyajyoti.group_management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GROUP_MEMBER_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberEntity extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    private GroupEntity group;

    @Column(name = "USER_ID")
    private BigInteger userId;

}
