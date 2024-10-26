package com.divyajyoti.group_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "USER_DETAILS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity implements Serializable {

    @Column(name = "USER_NAME")
    private String name;

    @Column(name = "USER_CONTACT")
    private String contact;

    @Column(name = "USER_EMAIL")
    private String email;

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private List<GroupEntity> groupsMemberList;

}
