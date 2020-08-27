package com.example.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 外部接口信息
 * </p>
 *
 * @author generator
 * @since 2020-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SourceInfo对象", description = "数据源信息")
public class EtlSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "数据源唯一标识")
    private String sourceKey;

    @ApiModelProperty(value = "数据源路径")
    private String sourceUrl;

    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    @ApiModelProperty(value = "传參类型0:form,1:json,2:urlEncoded")
    private Integer contentType;

    @ApiModelProperty(value = "result处理方式0:同步处理,1:异步处理,2:自定义处理")
    private Integer processMethod;

    @ApiModelProperty(value = "备注")
    private String remark;


}
