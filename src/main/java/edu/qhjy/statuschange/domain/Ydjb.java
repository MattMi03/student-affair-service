package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Ydjb {
    private Long ydjbbs;
    private String xxdm;
    private Integer jb;
    private Integer rs;
    private String kjzcReason;
    private String attachment;
    private Integer bType; // 1:省外, 2:省内
    private String shzt;
    private String shyj;
    private String cjrxm;
    private LocalDateTime cjsj;
    private String shrxm;
    private LocalDateTime shsj;
    private String shjd;
}