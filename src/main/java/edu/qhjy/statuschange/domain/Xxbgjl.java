package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Xxbgjl implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long ggbS;        // 变更标识ID
    private Long kjydjlbs;    // 考籍异动记录标识id (关联kjydjl表)
    private String ggsxmc;    // 变更事项名称 (如: 姓名, 身份证号)
    private String gqz;       // 变更前的值
    private String ghz;       // 变更后的值
    private String zmwjdz;    // 证明文件地址
    private String cjrxm;     // 创建人姓名
    private LocalDateTime cjsj;        // 创建时间
    private String gxrxm;     // 更新人姓名
    private LocalDateTime gxsj;        // 更新时间
}