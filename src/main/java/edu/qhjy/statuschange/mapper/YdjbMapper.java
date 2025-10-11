package edu.qhjy.statuschange.mapper;

import com.github.pagehelper.Page;
import edu.qhjy.statuschange.domain.Ydjb;
import edu.qhjy.statuschange.dto.remoteclass.YdjbQueryDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbStudentQueryDTO;
import edu.qhjy.statuschange.vo.remoteclass.YdjbListVO;
import edu.qhjy.statuschange.vo.remoteclass.YdjbStudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface YdjbMapper {
    int insert(Ydjb ydjb);

    int update(Ydjb ydjb);

    int deleteById(@Param("ydjbbs") Long ydjbbs);

    Ydjb findById(@Param("ydjbbs") Long ydjbbs);

    Page<YdjbListVO> findForPage(YdjbQueryDTO query);

    Page<YdjbStudentVO> findStudentsByYdjbbs(
            @Param("ydjbbs") Long ydjbbs,
            @Param("query") YdjbStudentQueryDTO query
    );

    int addStudentsToYdjb(
            @Param("ydjbbs") Long ydjbbs,
            @Param("ksbsList") List<Long> ksbsList
    );

    int removeStudentsFromYdjb(
            @Param("ydjbbs") Long ydjbbs,
            @Param("ksbsList") List<Long> ksbsList
    );

    void deleteBatchByIds(List<Long> ids);

    void clearStudentsFromYdjb(Long ydjbbs);
}