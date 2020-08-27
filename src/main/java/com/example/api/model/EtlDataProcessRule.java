package com.example.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 外部接口返回映射
 * </p>
 *
 * @author generator
 * @since 2020-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EtlDataProcessRule", description = "数据处理规则")
public class EtlDataProcessRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "接口标识")
    private String sourceKey;

    @ApiModelProperty(value = "结果键")
    private String dataKey;

    @ApiModelProperty(value = "是否需联查信息")
    private Boolean hasMapping;

    @ApiModelProperty(value = "联查信息设置")
    private Integer mappingId;

    @ApiModelProperty(value = "是否条件去重字段")
    private Boolean hasDistinct;

    @ApiModelProperty(value = "对应表")
    private String targetTable;

    @ApiModelProperty(value = "对应表字段")
    private String targetColumns;

    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    @ApiModelProperty(value = "执行顺序")
    private Integer execSort;

    @ApiModelProperty(value = "执行类型(insert,replace,update,delete,insert or update)")
    private String execType;

    @ApiModelProperty(value = "是否为条件字段")
    private Boolean isCondition;

    @ApiModelProperty(value = "条件字符(=,!=,<,<=)")
    private String conditionSymbol;

    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * not table field
     */
    @ApiModelProperty(value = "联查设置")
    private EtlDataProcessMapping etlDataProcessMapping;

    @ApiModelProperty("切割循环键")
    private List<String> splitKeyList;

    @ApiModelProperty("切割循环键个数")
    private Integer splitKeyListSize;

    public EtlDataProcessRule(String execType) {
        this.execType = execType;
    }

    public EtlDataProcessRule() {
    }

    public EtlDataProcessRule(String targetColumns, String execType,
                              Boolean isCondition, String conditionSymbol) {
        this.targetColumns = targetColumns;
        this.isCondition = isCondition;
        this.execType = execType;
        this.conditionSymbol = conditionSymbol;
    }
}
