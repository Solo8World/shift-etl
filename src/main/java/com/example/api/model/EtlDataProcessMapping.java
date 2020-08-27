package com.example.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 映射字段关联表字段信息
 * </p>
 *
 * @author generator
 * @since 2020-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EtlDataProcessMapping", description = "映射字段值设置")
public class EtlDataProcessMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "查询字段")
    private String resultField;

    @ApiModelProperty(value = "条件字段")
    private String mappingField;

    @ApiModelProperty(value = "表名")
    private String mappingTable;

    @ApiModelProperty(value = "是否预处理")
    private Boolean pretreatment;

    @ApiModelProperty(value = "预处理数据范围")
    private String pretreatmentRange;
    /**
     * not table field
     */
    @ApiModelProperty(value = "预查询数据")
    private Map<Object, Object> preQueryData;


}
