package com.example.api.dao;

import com.example.api.model.EtlDataProcessRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 外部接口返回映射(ClientResultMapping)表数据库访问层
 *
 * @author makejava
 * @since 2020-05-13 15:51:59
 */
@Mapper
public interface EtlDataProcessRuleMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param sourceKey 标识
     * @return 实例对象
     */
    @Select("<script>\n" +
            "            select\n" +
            "              id,source_key, data_key, has_mapping,mapping_id,has_distinct, \n" +
            "              target_table, target_columns, default_value, exec_sort, exec_type, \n" +
            "              is_condition, condition_symbol, remark\n" +
            "              from etl_data_process_rule\n" +
            "            where source_key = #{sourceKey}\n" +
            "            </script>")
    List<EtlDataProcessRule> selectBySourceKey(String sourceKey);




}