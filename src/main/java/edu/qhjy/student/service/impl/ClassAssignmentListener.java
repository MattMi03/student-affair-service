package edu.qhjy.student.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import edu.qhjy.student.dto.classmanager.ClassAssignmentData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入监听器：用于解析学生班级分配数据
 */
@Slf4j
@Getter
public class ClassAssignmentListener extends AnalysisEventListener<ClassAssignmentData> {

    /**
     * 存放解析出的数据
     */
    private final List<ClassAssignmentData> dataList = new ArrayList<>();

    /**
     * 每解析一行数据都会调用该方法
     */
    @Override
    public void invoke(ClassAssignmentData data, AnalysisContext context) {
        if (data == null) {
            return;
        }

        // 判断考籍号是否有值，作为有效行的判断标准
        if (StringUtils.hasText(data.getKsh())) {
            // 去除首尾空格
            data.setKsh(data.getKsh().trim());
            if (StringUtils.hasText(data.getXm())) {
                data.setXm(data.getXm().trim());
            }
            if (StringUtils.hasText(data.getSfzjh())) {
                data.setSfzjh(data.getSfzjh().trim());
            }
            if (StringUtils.hasText(data.getBjmc())) {
                data.setBjmc(data.getBjmc().trim());
            }

            dataList.add(data);
        } else {
            log.warn("跳过无效行：{}", data);
        }
    }

    /**
     * 所有数据解析完成后调用一次
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel 解析完成，共 {} 条有效数据", dataList.size());
    }
}