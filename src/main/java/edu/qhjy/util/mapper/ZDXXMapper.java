package edu.qhjy.util.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ZDXXMapper {

    @Select("SELECT ZDBS, ZDLX, ZDMC, XSXH FROM ZDXX")
    List<Map<String, Object>> selectAll();

    @Select("SELECT ZDBS, ZDLX, ZDMC, XSXH FROM ZDXX WHERE ZDLX = #{zdlx}")
    List<Map<String, Object>> selectByZDLX(Integer zdlx);
}