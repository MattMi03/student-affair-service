package edu.qhjy.statuschange.handler.impl;

import edu.qhjy.statuschange.domain.Zxjl;
import edu.qhjy.statuschange.handler.IStatusChangeHandler;
import edu.qhjy.statuschange.mapper.KsxxMapper;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 转学处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferHandler implements IStatusChangeHandler {

    private final KsxxMapper ksxxMapper;
    private final StatusChangeMapper statusChangeMapper;

    @Override
    public Long getActionType() {
        return 4L; // 4 = 转学
    }

    @Override
    public void apply(Long kjydjlbs) {
        // 1. 首先，根据 kjydjlbs 获取转学的详细信息
        Zxjl transferDetail = statusChangeMapper.findZxjlByKjydjlbs(kjydjlbs);
        if (transferDetail == null) {
            log.error("未找到 kjydjlbs 为 {} 的转学详情记录，无法执行善后操作。", kjydjlbs);
            return;
        }

        String ksh = statusChangeMapper.findKshByKjydjlbs(kjydjlbs);
        if (ksh == null) {
            log.error("未找到 kjydjlbs 为 {} 对应的考生号，无法执行善后操作。", kjydjlbs);
            return;
        }

        // 2. 【核心】根据转学类型 (ZXLX) 执行不同的逻辑
        Long zxlxLong = transferDetail.getZxlx();
        Integer zxlx = zxlxLong != null ? zxlxLong.intValue() : null;
        if (zxlx == null) {
            log.warn("kjydjlbs {} 的转学记录缺少转学类型(ZXLX)，跳过善后操作。", kjydjlbs);
            return;
        }

        switch (zxlx) {
            case 1: // 省内转出
            case 2: // 省内转入
                // 对于省内转学，无论是“转出”还是“转入”记录被触发，
                // 最终的善后操作都是将学生的学籍信息更新到“新学校”。
                log.info("执行省内转学善后：更新考生 {} 的学校信息。", ksh);
                ksxxMapper.updateStudentSchoolInfo(
                        ksh,
                        transferDetail.getXxmc(),  // 现学校名称
                        transferDetail.getJdbj(),  // 现就读班级
                        Integer.valueOf(transferDetail.getJdnj()) // 现就读年级
                );
                break;

            case 3: // 转出到省外
                // 对于转出到省外的学生，善后操作是更新其考籍状态。
                log.info("执行省外转出善后：更新考生 {} 的考籍状态为'转出'。", ksh);
                ksxxMapper.updateStudentStatus(ksh, "转出省外");
                break;

            case 4: // 省外转入
                // TODO
                log.info("接收到省外转入 (ZXLX=4) 的善后任务，记录ID: {}。此功能待实现(TODO)。", kjydjlbs);
                //  (未来在这里添加省外转入的逻辑)
                break;

            default:
                log.warn("未知的转学类型(ZXLX={})，无法执行善后操作，记录ID: {}", zxlx, kjydjlbs);
                break;
        }
    }
}