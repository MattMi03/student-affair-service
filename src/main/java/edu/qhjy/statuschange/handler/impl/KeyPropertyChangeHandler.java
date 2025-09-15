package edu.qhjy.statuschange.handler.impl;

import edu.qhjy.statuschange.domain.Xxbgjl;
import edu.qhjy.statuschange.handler.IStatusChangeHandler;
import edu.qhjy.statuschange.mapper.KsxxMapper;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关键信息变更处理器
 */
@Component
@RequiredArgsConstructor
public class KeyPropertyChangeHandler implements IStatusChangeHandler {

    private final KsxxMapper ksxxMapper;
    private final StatusChangeMapper statusChangeMapper;

    @Override
    public Long getActionType() {
        return 3L; // 3 = 信息变更
    }

    @Override
    public void apply(Long kjydjlbs) {
        String ksh = statusChangeMapper.findKshByKjydjlbs(kjydjlbs);
        if (ksh == null) return;

        List<Xxbgjl> changes = statusChangeMapper.findXxbgjlByKjydjlbs(kjydjlbs);
        for (Xxbgjl change : changes) {
            ksxxMapper.updateStudentKeyProperty(
                    ksh,
                    change.getGgsxmc(), // 变更事项名称 (例如: "姓名", "身份证件号")
                    change.getGhz()     // 变更后的值
            );
        }
    }
}