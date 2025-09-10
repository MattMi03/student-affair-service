package edu.qhjy.student.dto.registeration;

import lombok.Data;

@Data
public class GuardianInfoDTO {
    private String gx;            // 关系 (e.g., "父亲", "母亲")
    private String xm;            // 姓名
    private String sfzjh;         // 身份证号码
    private String mz;            // 民族
    private String yddh;          // 联系电话
    private String gzdwmc;        // 工作单位
    private String gzdwzw;        // 职务

    // 现户籍所在地
    private String szqmc;         // 现户籍所在省名称
    private String szsmc;         // 现户籍所在市名称
    private String szxmc;         // 现户籍所在县名称
}