package edu.qhjy.statuschange.handler.impl;

import edu.qhjy.statuschange.handler.IStatusChangeHandler;
import edu.qhjy.statuschange.mapper.KsxxMapper;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 流失处理器
 */
@Component
@RequiredArgsConstructor
public class AttritionHandler implements IStatusChangeHandler {
    private final KsxxMapper ksxxMapper;
    private final StatusChangeMapper statusChangeMapper;

    @Override
    public Long getActionType() {
        return 5L;
    } // 5 = 流失

    @Override
    public void apply(Long kjydjlbs) {
        String ksh = statusChangeMapper.findKshByKjydjlbs(kjydjlbs);
        if (ksh == null) return;
        ksxxMapper.updateStudentStatus(ksh, "流失");
    }
}