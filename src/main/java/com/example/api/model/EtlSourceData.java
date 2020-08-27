package com.example.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据拉取
 * </p>
 *
 * @author generator
 * @since 2020-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EtlSourceData", description = "数据源数据")
public class EtlSourceData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "接口key")
    private String sourceKey;

    @ApiModelProperty(value = "json内容")
    private String sourceData;

    @ApiModelProperty(value = "处理状态(0-等待处理  1.处理中,2-处理完成 3-处理失败)")
    private Integer processingStatus;

    @ApiModelProperty(value = "开始处理时间")
    private LocalDateTime actionDate;

    @ApiModelProperty(value = "处理耗时")
    private Long processTime;

    @ApiModelProperty(value = "处理结果")
    private String processResult;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateDate;


    public EtlSourceData() {
    }

    public EtlSourceData(String sourceKey, String sourceData, Integer processingStatus) {
        this.sourceKey = sourceKey;
        this.sourceData = sourceData;
        this.processingStatus = processingStatus;
    }

    public EtlSourceData(Long id, Integer processingStatus, LocalDateTime actionDate) {
        this.id = id;
        this.processingStatus = processingStatus;
        this.actionDate = actionDate;
    }

    public EtlSourceData(Long id, Integer processingStatus, String processResult, Long processTime) {
        this.id = id;
        this.processingStatus = processingStatus;
        this.processResult = processResult;
        this.processTime = processTime;
    }
}
