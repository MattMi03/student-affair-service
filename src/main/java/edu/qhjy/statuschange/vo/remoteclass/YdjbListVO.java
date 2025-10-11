package edu.qhjy.statuschange.vo.remoteclass;

import lombok.Data;

@Data
public class YdjbListVO {
    private Long ydjbbs;
    private String kqmc; // 考区名称
    private String xxmc; // 学校名称
    private Integer jb;
    private Integer totalStudents; // 总人数 (关联学生表的实际人数)
    private String shzt;
    private String shyj;
    private String shrxm;
    private String shjd;
    private String shsj;
    private String attachment;
}