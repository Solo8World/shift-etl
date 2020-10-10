package com.example.api.service;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.example.api.common.Constants;
import com.example.api.dao.EtlDataProcessRuleMapper;
import com.example.api.dao.EtlSourceDataMapper;
import com.example.api.exception.ResultException;
import com.example.api.model.EtlDataProcessRule;
import com.example.api.model.EtlSourceData;
import com.example.api.model.TableData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.api.common.Constants.*;


/**
 * 源数据处理服务
 *
 * @author lizhuo
 */
@Service
@Slf4j
public class EtlProcessService {

    @Resource
    private EtlSourceDataMapper etlSourceDataMapper;
    @Resource
    private EtlDataProcessRuleMapper etlDataProcessRuleMapper;
    @Resource
    private EtlTransformService etlTransformService;
    @Resource
    private EtlLoadService etlLoadService;


    public void processSourceData(String sourceKey) {
        final EtlSourceData etlSourceData
                = etlSourceDataMapper.selectOne(sourceKey, Constants.PROCESSING_STATUS_UNPROCESSED);
        if (ObjectUtils.isEmpty(etlSourceData)
                || StringUtils.isEmpty(etlSourceData.getSourceData())) {
            return;
        }
        final LocalDateTime actionDate = LocalDateTime.now();
        etlSourceDataMapper.update(
                new EtlSourceData(etlSourceData.getId(), Constants.PROCESSING_STATUS_PENDING, actionDate));

        String msg = "处理完成";
        Integer processStatus = PROCESSING_STATUS_COMPLETED;
        try {
            processSourceData(sourceKey, JSONUtil.parse(etlSourceData.getSourceData()));
        } catch (Exception e) {
            e.printStackTrace();
            msg = e.toString();
            processStatus = PROCESSING_STATUS_FAIL;
        }
        Duration duration = Duration.between(actionDate, LocalDateTime.now());
        etlSourceDataMapper.update(
                new EtlSourceData(etlSourceData.getId(), processStatus, msg, duration.toMillis()));
    }


    /**
     * 处理json
     *
     * @param sourceKey 接口标识
     * @param result    json数据
     */
    public void processSourceData(String sourceKey, JSON result) {
/*        final String replace = result.toString().replace("\"[", "[")
                .replace("]\"", "]").replace("\\", "");*/
        final JSON sourceDataJson = JSONUtil.parse(result.toString());
        //查询当前接口映射规则
        final List<EtlDataProcessRule> processRules =
                etlDataProcessRuleMapper.selectBySourceKey(sourceKey);
        //验证映射规则设置
        if (!validProcessRule(processRules)) {
            return;
        }
        //分割映射key
        processRules.forEach(this::splitRuleDataKey);

        //根据执行顺序分组映射规则
        final Map<Integer, List<EtlDataProcessRule>> sortGroup =
                processRules.stream()
                        .collect(Collectors.groupingBy(EtlDataProcessRule::getExecSort));

        //循环处理映射
        sortGroup.forEach((key, value) -> {
            final List<Map<String, Object>> data = etlTransformService.transformData(value, sourceDataJson);

            //插入解析数据
            if (!CollectionUtils.isEmpty(data)) {
                etlLoadService.loadData(new TableData(value.get(0).getTargetTable(), data), value);
            }
        });
    }

    private void splitRuleDataKey(EtlDataProcessRule processRule) {
        final String resultKey = StringUtils.trimAllWhitespace(processRule.getDataKey());
        processRule.setSplitKeyList(new ArrayList<>(Arrays.asList(resultKey.split(Constants.ARRAY_EXPRESSION))));
        processRule.setSplitKeyListSize(processRule.getSplitKeyList().size());
    }

    /**Detected default credentials 'minioadmin:minioadmin', please change the credentials immediately using 'MINIO_ACCESS_KEY' and 'MINIO_SECRET_KEY'

     * 检查处理规则设置
     *
     * @param processRules rule
     */
    private boolean validProcessRule(List<EtlDataProcessRule> processRules) {
        if (CollectionUtils.isEmpty(processRules)) {
            log.info("未配置相应映射规则");
            return true;
        }
        processRules.forEach(i -> {
            i.setExecType(i.getExecType().toLowerCase().replaceAll("\\s*", ""));
            if (!EXEC_ALL.contains(i.getExecType())) {
                throw new ResultException("不支持的执行类型:" + i.getExecType(),
                        HttpStatus.NOT_ACCEPTABLE.value());
            }
            if (i.getIsCondition()) {
                if (StringUtils.isEmpty(i.getConditionSymbol())) {
                    throw new ResultException("条件字段的条件比较字符不能为空",
                            HttpStatus.NOT_ACCEPTABLE.value());
                }
                i.setConditionSymbol(i.getConditionSymbol().toLowerCase().replaceAll("\\s*", ""));
                if (!CONDITION_ALL.contains(i.getConditionSymbol())) {
                    throw new ResultException("不支持的条件字符:" + i.getConditionSymbol(),
                            HttpStatus.NOT_ACCEPTABLE.value());
                }
            }
        });
        return true;
    }
}
