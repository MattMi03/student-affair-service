package edu.qhjy.statuschange.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CommonQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String ksh; // 考籍号
    private String xm;  // 姓名
    private String sfzjh; // 身份证件号

    @JsonIgnore
    private String permissionDm;

    private int pageNum = 1; // 页码
    private int pageSize = 10; // 每页条数
}
