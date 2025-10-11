package edu.qhjy.statuschange.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.statuschange.domain.Ydjb;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.remoteclass.AssignStudentsDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbQueryDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbStudentQueryDTO;
import edu.qhjy.statuschange.vo.remoteclass.YdjbListVO;
import edu.qhjy.statuschange.vo.remoteclass.YdjbStudentVO;

import java.util.List;

public interface YdjbService {
    Ydjb createYdjb(YdjbDTO dto, Integer bType, String creatorName);

    void deleteYdjb(Long ydjbbs);

    void updateYdjb(Long ydjbbs, YdjbDTO dto);

    Ydjb getYdjbById(Long ydjbbs);

    PageInfo<YdjbListVO> listYdjb(YdjbQueryDTO query, int pageNum, int pageSize);

    void deleteYdjbBatch(List<Long> ids);

    PageInfo<YdjbStudentVO> listStudents(Long id, YdjbStudentQueryDTO query, int pageNum, int pageSize);

    void addStudents(Long id, AssignStudentsDTO dto);

    void removeStudents(Long id, AssignStudentsDTO dto);

    void auditYdjb(Long id, AuditRequestDTO payload);
}