package edu.qhjy.statuschange.dto.audit;

import lombok.Data;

/**
 * 封装当前登录用户身份信息的 DTO
 */
@Data
public class UserInfo {
    private String name;      // 用户名
    private String groupId;
    private String dm;
    private String dzm;
}
