package edu.qhjy.punchin.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Dkjl {

    private Long dkjlbs;           // 打卡记录标识id
    private Long dkjhbs;           // 打卡计划标识
    private LocalDate dkrq;        // 打卡日期
    private String ksh;            // 考生号
    private LocalDateTime dksj;    // 打卡时间
    private String dksb;           // 打卡设备
    private String dkdd;           // 打卡地点
    private BigDecimal dkddjd;     // 打卡地点经度
    private BigDecimal dkddwd;     // 打卡地点纬度
    private String dktxdz;         // 打卡图像存储地址
    private String shjd;           // 打卡状态
    private String shzt;           // 审核状态
    private LocalDateTime shsj;    // 审核时间
    private String shrxm;          // 审核人姓名
    private String shyj;           // 审核意见
    private String cjrxm;          // 创建人姓名
    private String cjrgzrym;       // 创建人工作人员码
    private LocalDateTime cjsj;    // 创建时间
    private String gxrxm;          // 更新人姓名
    private String gxrgzrym;       // 更新人工作人员码
    private LocalDateTime gxsj;    // 更新时间
}