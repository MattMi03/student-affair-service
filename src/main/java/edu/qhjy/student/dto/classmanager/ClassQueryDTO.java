package edu.qhjy.student.dto.classmanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ClassQueryDTO {

    // --- 查询条件 ---
    private String szsdm;
    private String kqdm; // 考区代码
    private String xxdm;   // 学校标识ID
    private Integer jbny; // 级别


    @JsonIgnore
    private String permissionDm;

    // 分页参数
    private int pageNum = 1;
    private int pageSize = 10;
}