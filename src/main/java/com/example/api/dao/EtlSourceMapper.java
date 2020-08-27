package com.example.api.dao;

import com.example.api.model.EtlSource;
import com.example.api.model.EtlSourceValid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 数据源信息表 数据库访问层
 *
 * @author makejava
 * @since 2020-05-13 15:50:49
 */
@Mapper
public interface EtlSourceMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param key 标识
     * @return 实例对象
     */
    @Select("    <script>\n" +
            "            select\n" +
            "              id,source_key, source_url, request_method, process_method,content_type, remark from etl_source\n" +
            "            where source_key = #{key}\n" +
            "            </script>" )
    EtlSource selectEtlSourceByKey(String key);


    /**
     * 通过ID查询单条数据
     *
     * @param sourceId 数据源主键
     * @return 实例对象
     */
    @Select("<script>\n" +
            "            select\n" +
            "              id,source_id, code_key, success_code, msg_key, data_key, remark       from etl_source_valid\n" +
            "            where source_id = #{sourceId}\n" +
            "            </script>")
    EtlSourceValid selectEtlSourceValidBySourceId(Integer sourceId);

}