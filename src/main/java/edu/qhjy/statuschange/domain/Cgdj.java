package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Cgdj implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long cgdjbs;        // 出国登记标识id
    private Long kjydjlbs;
    private String xxmc;         // 学校
    private String cggj;         // 出国国家
    private String clrxm;        // 处理人
    private LocalDate djsj;  // 登记时间
    private String cgyy;         // 出国原因
    private String bz;           // 备注
    private String cjrxm;        // 创建人姓名
    private String cjrgzrym;     // 创建人工作人员码
    private LocalDateTime cjsj;  // 创建时间
    private String gxrxm;        // 更新人姓名
    private String gxrgzrym;     // 更新人工作人员码
    private LocalDateTime gxsj;  // 更新时间
}