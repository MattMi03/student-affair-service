package edu.qhjy.statuschange.handler.impl;

import edu.qhjy.statuschange.handler.IStatusChangeHandler;
import edu.qhjy.statuschange.mapper.KsxxMapper;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 补录处理器
 */
@Component
@RequiredArgsConstructor
public class LateRegistrationHandler implements IStatusChangeHandler {
    private final KsxxMapper ksxxMapper;
    private final StatusChangeMapper statusChangeMapper;

    @Override
    public Long getActionType() {
        return 6L;
    } // 6 = 补录

    @Override
    public void apply(Long kjydjlbs) {
        String ksh = statusChangeMapper.findKshByKjydjlbs(kjydjlbs);
        if (ksh == null) return;
        ksxxMapper.updateStudentStatus(ksh, "正常在校");
    }
}