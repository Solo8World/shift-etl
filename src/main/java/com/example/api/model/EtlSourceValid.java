package com.example.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 外部接口返回信息
 * </p>
 *
 * @author generator
 * @since 2020-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EtlSourceValid", description = "外部接口返回数据有效性验证")
public class EtlSourceValid implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "接口标识")
    private String sourceId;

    @ApiModelProperty(value = "请求状态字段")
    private String codeKey;

    @ApiModelProperty(value = "成功状态码")
    private String successCode;

    @ApiModelProperty(value = "请求信息字段")
    private String msgKey;

    @ApiModelProperty(value = "数据字段")
    private String dataKey;

    @ApiModelProperty(value = "备注")
    private String remark;


}
