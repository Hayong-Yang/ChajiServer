//MemberDTO
package com.highfive.chajiserver.dto;

import lombok.Data;

@Data
public class MemberDTO {
    private int idx;
    private String userId;
    private String password;
    private String userName;
}
