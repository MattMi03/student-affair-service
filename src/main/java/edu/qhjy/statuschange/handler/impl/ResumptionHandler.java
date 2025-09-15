package edu.qhjy.statuschange.handler.impl;

import edu.qhjy.statuschange.domain.Xfxjl;
import edu.qhjy.statuschange.handler.IStatusChangeHandler;
import edu.qhjy.statuschange.mapper.KsxxMapper;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 复学处理器
 */
@Component
@RequiredArgsConstructor
public class ResumptionHandler implements IStatusChangeHandler {

    private final KsxxMapper ksxxMapper;
    private final StatusChangeMapper statusChangeMapper;

    @Override
    public Long getActionType() {
        return 2L; // 2 = 复学
    }

    @Override
    public void apply(Long kjydjlbs) {
        Xfxjl resumptionDetail = statusChangeMapper.findXfxjlByKjydjlbs(kjydjlbs);
        if (resumptionDetail == null) return;

        String ksh = statusChangeMapper.findKshByKjydjlbs(kjydjlbs);
        if (ksh == null) return;

        ksxxMapper.updateStudentStatus(ksh, "正常在校");
        ksxxMapper.updateStudentResumptionInfo(
                ksh,
                resumptionDetail.getXbjmc(), // 新班级名
                Integer.valueOf(resumptionDetail.getXjdnj())
        );
    }
}