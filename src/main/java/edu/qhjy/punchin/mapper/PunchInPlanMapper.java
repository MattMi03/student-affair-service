package edu.qhjy.punchin.mapper;

import edu.qhjy.punchin.domain.Dkjh;
import edu.qhjy.punchin.dto.PunchInPlanQueryDTO;
import edu.qhjy.punchin.dto.PunchInPlanSubmitDTO;
import edu.qhjy.punchin.vo.PunchInPlanListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PunchInPlanMapper {

    List<PunchInPlanListVO> findForPage(PunchInPlanQueryDTO query);

    Dkjh findById(@Param("dkjhbs") Long dkjhbs);

    void insertPlan(Dkjh plan);

    void batchInsertPlanDates(@Param("dkjhbs") Long dkjhbs, @Param("punchDates") List<LocalDate> punchDates);

    void updatePlan(Dkjh plan);

    void deletePlanDatesByPlanId(@Param("dkjhbs") Long dkjhbs);

    void deletePlanById(@Param("dkjhbs") Long dkjhbs);

    int findSchoolCountByCode(String xxdm);

    Dkjh findBySubmitDTO(PunchInPlanSubmitDTO dto);

    List<LocalDate> findPlanDatesByPlanId(Long dkjhbs);

    List<String> findAllSemesters();
}