package com.example.api.dao;

import com.example.api.model.EtlSourceData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 数据拉取(EtlSourceData)表数据库访问层
 *
 * @author makejava
 * @since 2020-05-13 15:51:59
 */
@Mapper
public interface EtlSourceDataMapper {


    @Select(" <script>\n" +
            "               select\n" +
            "              id,source_key, source_data, processing_status, action_date, process_time, process_result,\n" +
            "              create_date, update_date\t        from etl_source_data\n" +
            "            where source_key = #{sourceKey} and processing_status= #{processingStatus}\n" +
            "            order by create_date limit 1\n" +
            "            </script>")
    EtlSourceData selectOne(String sourceKey, Integer processingStatus);


    /**
     * 新增数据
     *
     * @param etlSourceData 实例对象
     * @return 影响行数
     */
    @Insert("  <script>\n" +
            "            insert into etl_source_data\n" +
            "            <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
            "                      <if test=\"sourceKey != null and sourceKey != ''\">\n" +
            "                          source_key,\n" +
            "                      </if>\n" +
            "                      <if test=\"sourceData != null and sourceData != ''\">\n" +
            "                          source_data,\n" +
            "                      </if>\n" +
            "                      <if test=\"processingStatus != null\">\n" +
            "                          processing_status,\n" +
            "                      </if>\n" +
            "                      <if test=\"actionDate != null\">\n" +
            "                          action_date,\n" +
            "                      </if>\n" +
            "                      <if test=\"processTime != null\">\n" +
            "                          process_time,\n" +
            "                      </if>\n" +
            "                      <if test=\"processResult != null and processResult != ''\">\n" +
            "                          process_result,\n" +
            "                      </if>\n" +
            "                      <if test=\"createDate != null\">\n" +
            "                          create_date,\n" +
            "                      </if>\n" +
            "                      <if test=\"updateDate != null\">\n" +
            "                          update_date,\n" +
            "                      </if>\n" +
            "            </trim>\n" +
            "                  <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n" +
            "                  <if test=\"sourceKey != null and sourceKey != ''\">\n" +
            "                      #{sourceKey},\n" +
            "                  </if>\n" +
            "                  <if test=\"sourceData != null and sourceData != ''\">\n" +
            "                      #{sourceData},\n" +
            "                  </if>\n" +
            "                  <if test=\"processingStatus != null\">\n" +
            "                      #{processingStatus},\n" +
            "                  </if>\n" +
            "                  <if test=\"actionDate != null\">\n" +
            "                      #{actionDate},\n" +
            "                  </if>\n" +
            "                  <if test=\"processTime != null\">\n" +
            "                      #{processTime},\n" +
            "                  </if>\n" +
            "                  <if test=\"processResult != null and processResult != ''\">\n" +
            "                      #{processResult},\n" +
            "                  </if>\n" +
            "                  <if test=\"createDate != null\">\n" +
            "                      #{createDate},\n" +
            "                  </if>\n" +
            "                  <if test=\"updateDate != null\">\n" +
            "                      #{updateDate},\n" +
            "                  </if>\n" +
            "              </trim>\n" +
            "              </script>")
    int insert(EtlSourceData etlSourceData);

    /**
     * 修改数据
     *
     * @param etlSourceData 实例对象
     * @return 影响行数
     */
    @Update("<script>\n" +
            "            \tupdate etl_source_data\n" +
            "                <set>\n" +
            "                      <if test=\"sourceKey != null and sourceKey != ''\">\n" +
            "                        source_key = #{sourceKey},\n" +
            "                    </if>\n" +
            "                      <if test=\"sourceData != null and sourceData != ''\">\n" +
            "                        source_data = #{sourceData},\n" +
            "                    </if>\n" +
            "                      <if test=\"processingStatus != null\">\n" +
            "                        processing_status = #{processingStatus},\n" +
            "                    </if>\n" +
            "                      <if test=\"actionDate != null\">\n" +
            "                        action_date = #{actionDate},\n" +
            "                    </if>\n" +
            "                      <if test=\"processTime != null\">\n" +
            "                        process_time = #{processTime},\n" +
            "                    </if>\n" +
            "                      <if test=\"processResult != null and processResult != ''\">\n" +
            "                        process_result = #{processResult},\n" +
            "                    </if>\n" +
            "                      <if test=\"createDate != null\">\n" +
            "                        create_date = #{createDate},\n" +
            "                    </if>\n" +
            "                      <if test=\"updateDate != null\">\n" +
            "                        update_date = #{updateDate},\n" +
            "                    </if>\n" +
            "                  </set>\n" +
            "                where id = #{id}\n" +
            "                </script>")
    int update(EtlSourceData etlSourceData);

}