package edu.qhjy.statuschange.dto.remoteclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class YdjbQueryDTO {
    private String szsdm; // 地市代码
    private String kqdm;  // 考区代码
    private String xxdm;  // 学校代码
    private Integer jb;   // 年级
    private Integer bType; // 1:省外, 2:省内

    @JsonIgnore
    private String permissionDm; // 权限代码
}