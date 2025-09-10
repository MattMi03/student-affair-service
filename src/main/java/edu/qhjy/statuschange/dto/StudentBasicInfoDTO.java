package edu.qhjy.statuschange.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class StudentBasicInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "考生号不能为空")
    private String ksh;
    @NotBlank(message = "姓名不能为空")
    private String xm;
    private String xb;
    private String mz;
    @NotBlank(message = "身份证件号不能为空")
    private String sfzjh;
    private String zbmc;
}
