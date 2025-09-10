// 文件路径: .../statuschange/vo/AbroadListVO.java
package edu.qhjy.statuschange.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AbroadListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cgdjbs;      // 记录ID (用于操作)
    private String ksh;         // 考籍号
    private String xm;          // 姓名
    private String sfzjh;       // 身份证件号
    private String xxmc;        // 学校
    private String cggj;        // 出国国家
    private String clrxm;       // 处理人
    private LocalDateTime djsj; // 登记时间
    private String cgyy;        // 出国原因
    private String bz;          // 备注
    private String mz;
    private String xb;
    private String zbmc;
}