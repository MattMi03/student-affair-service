package edu.qhjy.rollcall.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import edu.qhjy.rollcall.vo.StudentRollCallListVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RollCallImportListener extends AnalysisEventListener<StudentRollCallListVO> {

    private final IStudentRollCallService studentRollCallService;
    private final List<StudentRollCallListVO> dataList = new ArrayList<>();

    // 用于存储最终的导入结果
    @Getter
    private Map<String, Object> importResult;

    public RollCallImportListener(IStudentRollCallService studentRollCallService) {
        this.studentRollCallService = studentRollCallService;
    }

    @Override
    public void invoke(StudentRollCallListVO data, AnalysisContext context) {
        // 暂存读取到的每一行数据
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel文件解析完成，共读取到 {} 条数据，开始进行业务处理...", dataList.size());
        if (!dataList.isEmpty()) {
            // 调用 Service 层方法，执行核心的校验和批量更新逻辑
            this.importResult = studentRollCallService.processExcelData(dataList);
        } else {
            log.warn("Excel文件为空或内容无效。");
        }
        log.info("业务处理完成！");
    }
}