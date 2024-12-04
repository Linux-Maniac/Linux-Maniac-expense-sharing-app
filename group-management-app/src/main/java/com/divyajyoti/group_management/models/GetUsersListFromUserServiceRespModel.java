package com.divyajyoti.group_management.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersListFromUserServiceRespModel {

    private String status;

    private String message;

    private Details details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {

        private List<UserModel> usersList;

        private Integer totalRecords;

    }

}
