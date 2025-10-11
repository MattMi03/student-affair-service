package edu.qhjy.qzsh.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.qzsh.domain.Qzsh;
import edu.qhjy.qzsh.dto.QzshQueryDTO;
import edu.qhjy.qzsh.dto.QzshUpsertDTO;
import edu.qhjy.qzsh.vo.QzshListVO;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;

import java.util.List;

public interface QzshService {
    PageInfo<QzshListVO> listQzsh(QzshQueryDTO query);

    Qzsh getQzshById(Long ksbs);

    Qzsh createOrUpdateQzsh(QzshUpsertDTO dto);

    void deleteQzsh(Long ksbs);

    void deleteQzshBatch(List<Long> ids);

    void audit(Long ksbs, AuditRequestDTO auditRequestDTO);
}