package com.example.api.dao;

import com.example.api.model.Conditions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author lizhuo
 */
@Mapper
public interface EtlLoadMapper {

    void insertOrReplace(@Param("execType") String execType,
                         @Param("tableName") String tableName,
                         @Param("data") List<Map<String, Object>> data,
                         @Param("map") Map<String, Object> keyMap);

    void update(@Param("tableName") String tableName,
                @Param("map") Map<String, Object> keyMap,
                @Param("conditionsList") List<Conditions> conditionsList);

    void deleteInBatch(@Param("tableName") String tableName,
                       @Param("columns") String columns,
                       @Param("conditionsValues") List<Object> conditionsValues);

    void deleteOrBatch(@Param("tableName") String tableName,
                       @Param("conditionsListBatch") List<List<Conditions>> conditionsListBatch);

    List<Object> selectByConditionIn(@Param("tableName") String tableName,
                                     @Param("columns") String columns,
                                     @Param("conditionsValues") List<Object> conditionsValues);

    List<Map<String, Object>> selectByConditionOr(@Param("tableName") String tableName,
                                                  @Param("conditionsListBatch") List<List<Conditions>> conditionsListBatch);


    @Select("${sql}")
    Object execSqlSignValue(String sql);

    @Select("${sql}")
    List<Object> execSqlListValue(String sql);

    @Update("${sql}")
    void execSqlVoid(String sql);
}
