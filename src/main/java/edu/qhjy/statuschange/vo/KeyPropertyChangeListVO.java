package edu.qhjy.statuschange.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class KeyPropertyChangeListVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long kjydjlbs;      // 异动记录ID
    private String ggsxmc;      // 属性名称
    private String ksh;         // 考籍号
    private String xm;          // 姓名
    private String jb;          // 级别
    private String bjmc;        // 班级
    private String sfzjh;       // 身份证件号
    private String gqz;         // 修改前的值
    private String ghz;         // 修改后的值
    private String zmwjdz;      // 证明文件地址
    private LocalDateTime czsj; // 创建时间
    private String shzt;        // 审核状态
    private String shyj;        // 审核意见
    private String mz;
    private String xb;
    private String zbmc;
}