package com.divyajyoti.group_management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GROUP_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupEntity extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GROUP_ID")
    private BigInteger id;

    @Column(name = "GROUP_NAME")
    private String name;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "USER_GROUP_RELATIONS")
    private List<UserEntity> members;

}
