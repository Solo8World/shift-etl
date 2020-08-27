package com.example.api.dao;

import com.example.api.model.EtlDataProcessMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 映射字段关联表字段信息(ClientResultMappingRelation)表数据库访问层
 *
 * @author makejava
 * @since 2020-05-13 15:51:59
 */
@Mapper
public interface EtlDataProcessMappingMapper {


    @Select("<script>\n" +
            "                select\n" +
            "                 id, result_field, mapping_field, mapping_table, pretreatment,pretreatment_range\n" +
            "               from etl_data_process_mapping\n" +
            "               where  id in \n" +
            "               <foreach collection=\"ids\" item=\"item\" open=\"(\" close=\")\" separator=\",\">\n" +
            "                       #{item}\n" +
            "               </foreach>\n" +
            "            </script>")
    List<EtlDataProcessMapping> selectByIds(@Param("ids") List<Integer> ids);


    @Select("<script>\n" +
            "                select\n" +
            "                 ${mappingField},${resultField}\n" +
            "               from ${mappingTable}\n" +
            "               <if test=\"pretreatmentRange != null and pretreatmentRange != ''\">\n" +
            "               where  ${pretreatmentRange}\n" +
            "               </if>\n" +
            "            </script>")
    List<Map<Object, Object>> queryPreData(EtlDataProcessMapping setting);


    @Select("<script>\n" +
            "                select\n" +
            "                 ${mappingRelation.resultField}\n" +
            "               from ${mappingRelation.mappingTable}\n" +
            "               where  ${mappingRelation.mappingField} = #{mappingValue}\n" +
            "            </script>")
    Object queryData(@Param("mappingRelation") EtlDataProcessMapping mappingRelation,
                     @Param("mappingValue") Object mappingValue);


}