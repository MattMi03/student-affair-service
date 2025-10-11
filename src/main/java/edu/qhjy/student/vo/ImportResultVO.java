package edu.qhjy.student.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportResultVO {
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> errorMessages;
    private List<String> warningMessages; // [NEW] 新增警告信息列表
}