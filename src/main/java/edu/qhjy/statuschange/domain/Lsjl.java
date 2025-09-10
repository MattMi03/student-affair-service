package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Lsjl implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long lsjlbs;      // 流失记录标识id
    private Long kjydjlbs;    // 考籍异动记录标识id
    private String lsyy;      // 流失原因
    private LocalDate lssj;        // 流失时间
    private String cjrxm;     // 创建人姓名
    private String cjrgzrym;  // 创建人工作人员码
    private LocalDateTime cjsj;        // 创建时间
    private String gxrxm;     // 更新人姓名
    private String gxrgzrym;  // 更新人工作人员码
    private LocalDateTime gxsj;        // 更新时间
}
