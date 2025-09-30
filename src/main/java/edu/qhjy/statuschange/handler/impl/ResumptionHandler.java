package edu.qhjy.statuschange.handler.impl;

import edu.qhjy.statuschange.domain.Xfxjl;
import edu.qhjy.statuschange.handler.IStatusChangeHandler;
import edu.qhjy.statuschange.mapper.KsxxMapper;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

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
                Integer.valueOf(resumptionDetail.getXjdnj()),
                resumptionDetail.getXbjbs()
        );

        String newKsh = createNewKsh(ksh, resumptionDetail.getXjdnj().substring(2));
        String xxsc = calculateXxsc(resumptionDetail.getXxrq(), resumptionDetail.getFxrq());

        statusChangeMapper.setFxkshByKjydjlbs(kjydjlbs, newKsh, xxsc);

    }

    private String createNewKsh(String oldKsh, String newGrade) {
//        246301020120006  年级（2位）+学校代码（9位）+序号(4位)
        String schoolCode = oldKsh.substring(2, 11);
        String newKshPrefix = newGrade + schoolCode;

        String maxKsh = ksxxMapper.findMaxKshByPrefix(newKshPrefix);
        if (maxKsh == null) {
            return newKshPrefix + "0001";
        } else {
            int newSerial = Integer.parseInt(maxKsh.substring(11)) + 1;
            return newKshPrefix + String.format("%04d", newSerial);
        }
    }

    private String calculateXxsc(LocalDate xxrq, LocalDate fxrq) {
        if (xxrq == null || fxrq == null || !fxrq.isAfter(xxrq)) {
            return "0";
        }

        // 使用 Period.between 计算两个日期之间的差值
        Period period = Period.between(xxrq, fxrq);

        int years = period.getYears();
        int months = period.getMonths();

        // 如果总月数大于0但不足1年，进行格式化
        if (years == 0 && months > 0) {
            return months + "月";
        }

        // 如果超过1年，格式化为“X年Y月”
        if (years > 0) {
            // 如果月份为0，可以只显示年
            if (months == 0) {
                return years + "年";
            }
            return years + "年" + months + "月";
        }

        // 如果相差不足一个月，可以根据业务需求返回 "0" 或更精确的描述
        return "0";
    }
}