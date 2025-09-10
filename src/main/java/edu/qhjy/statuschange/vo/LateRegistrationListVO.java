// 文件路径: .../statuschange/vo/LateRegistrationListVO.java
package edu.qhjy.statuschange.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class LateRegistrationListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long kjydjlbs;      // 异动记录ID (用于操作)
    private String ksh;         // 考籍号
    private String xm;          // 姓名
    private String sfzjh;       // 身份证件号
    private Integer jb;       // 级别
    private String bjmc;        // 班级
    private LocalDate blsj; // 补录时间 (即申请创建时间)
    private String blxx;        // 补录学校
    private String shzt;        // 审核状态
    private String shyj;        // 审核意见
    private String mz;
    private String xb;
    private String zbmc;
}