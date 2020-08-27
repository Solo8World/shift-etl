package com.example.api.service;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.api.dao.EtlDataProcessMappingMapper;
import com.example.api.exception.ResultException;
import com.example.api.model.EtlDataProcessMapping;
import com.example.api.model.EtlDataProcessRule;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.api.common.Constants.*;


/**
 * 源数据转换 服务层
 *
 * @author lizhuo
 */
@Service
public class EtlTransformService {

    @Resource
    private EtlDataProcessMappingMapper etlDataProcessMappingMapper;


    private static final String NULL_STR = "null";

    public List<Map<String, Object>> transformData(List<EtlDataProcessRule> rules, JSON sourceDataJson) {
        //校验当前操作顺序下表唯一性
        validRulesSingleTable(rules);

        //赋值关联映射规则
        setMappingValue(rules);

        //根据分割次数(层级)排序
        final List<EtlDataProcessRule> sortedRules =
                rules.stream().sorted(
                        Comparator.comparing(EtlDataProcessRule::getSplitKeyListSize))
                        .collect(Collectors.toList());
        //根据映射规则解析数据
        return analyticalData(sourceDataJson, sortedRules);
    }

    /**
     * 赋值关联查询映射
     *
     * @param rules 总处理规则
     */
    private void setMappingValue(List<EtlDataProcessRule> rules) {
        final List<Integer> mappingIds = rules.stream()
                .filter(EtlDataProcessRule::getHasMapping)
                .map(EtlDataProcessRule::getMappingId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(mappingIds)) {
            return;
        }

        final List<EtlDataProcessMapping> mappings =
                etlDataProcessMappingMapper.selectByIds(mappingIds);

        mappings.stream()
                .filter(EtlDataProcessMapping::getPretreatment)
                .forEach(i -> i.setPreQueryData(getPreData(i)));

        mappings.forEach(r -> rules.stream()
                .filter(i -> !ObjectUtils.isEmpty(i.getMappingId())
                             && i.getMappingId().equals(r.getId()))
                .findFirst()
                .ifPresent(etlDataProcessRule -> etlDataProcessRule.setEtlDataProcessMapping(r)));
    }

    /**
     * 获取预处理数据
     *
     * @param mapping 映射关联关系
     * @return 预处理数据
     */
    private Map<Object, Object> getPreData(EtlDataProcessMapping mapping) {
        final List<Map<Object, Object>> queryPreData = etlDataProcessMappingMapper.queryPreData(mapping);
        Map<Object, Object> preData = new HashMap<>(queryPreData.size());
        final String resultField = mapping.getResultField();
        final String mappingField = mapping.getMappingField();
        queryPreData.forEach(i -> preData.put(i.get(mappingField), i.get(resultField)));
        return preData;
    }


    private List<Map<String, Object>> analyticalData(JSON sourceDataJson, List<EtlDataProcessRule> processRules) {
        //根据层级分组
        final Map<Integer, List<EtlDataProcessRule>> levelGroup =
                processRules.stream()
                        .collect(Collectors.groupingBy(EtlDataProcessRule::getSplitKeyListSize));
        //根据层级升序排列
        final LinkedHashMap<Integer, List<EtlDataProcessRule>> levelGroupSortedMappings =
                levelGroup.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<Map<String, Object>> dataSets = new ArrayList<>();
        getData(levelGroupSortedMappings, sourceDataJson, new HashMap<>(processRules.size()), dataSets);
        //数据条件去重+去空
        distinct(dataSets, processRules);
        return dataSets;
    }


    private void getData(Map<Integer, List<EtlDataProcessRule>> levelGroupSortedRules,
                         Object sourceData, Map<String, Object> data,
                         List<Map<String, Object>> dataSets) {
        //是否已读完
        if (levelGroupSortedRules.isEmpty()) {
            dataSets.add(data);
            return;
        }
        //按key值从少到多 从上层到下层 循环读取
        final Integer levelKey = levelGroupSortedRules.keySet().stream().findFirst().get();
        final List<EtlDataProcessRule> rules = levelGroupSortedRules.get(levelKey);

        // 循环当前级别下
        final JSON json = JSONUtil.parse(sourceData);

        //判断是否触底
        if (rules.get(0).getSplitKeyList().size() == 1) {

            //已触底 获取当前级别下映射值 并放入map
            rules.forEach(i -> data.put(i.getTargetColumns(), getValueByRulesNotJsonNull(json, i)));
            //干掉当前读取完的级别组
            levelGroupSortedRules.remove(levelKey);

            getData(levelGroupSortedRules, sourceData, data, dataSets);
        } else {
            //未触底
            //获取当前key
            final String expression = rules.get(0).getSplitKeyList().get(0);
            //根据当前key获取jsonArray
            final JSONArray jsonArray = JSONUtil.parseArray(json.getByPath(expression));
            //干掉已读的key
            levelGroupSortedRules.forEach((k, v) ->
                    v.forEach(i -> i.getSplitKeyList().remove(0)));
            //循环获取到的jsonArray
            jsonArray.forEach(i -> {
                //递归 深度拷贝 分发调用
                getData(deepClone(levelGroupSortedRules), i, new HashMap<>(data), dataSets);
            });
        }
    }

    /**
     * 数据条件去重
     *
     * @param dataSets    数据
     * @param sortedValue 映射规则设置
     */
    private void distinct(List<Map<String, Object>> dataSets, List<EtlDataProcessRule> sortedValue) {
        final List<String> hasDistinctColumns =
                sortedValue.stream()
                        .filter(EtlDataProcessRule::getHasDistinct)
                        .map(EtlDataProcessRule::getTargetColumns)
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(hasDistinctColumns)) {
            return;
        }
        Set<String> keysSet = new HashSet<>(dataSets.size());
        Iterator<Map<String, Object>> it = dataSets.iterator();
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            StringBuilder unique = new StringBuilder();
            hasDistinctColumns.forEach(j -> unique.append(map.get(j)));

            if (!unique.toString().contains(NULL_STR)
                && keysSet.add(unique.toString())) {
                continue;
            }
            it.remove();
        }
    }

    private Object getValueByRulesNotJsonNull(JSON json, EtlDataProcessRule rules) {
        final Object value = getValueByRules(json, rules);
        return ObjectUtils.isEmpty(value) || NULL_STR.equals(value.toString()) ? null : value;
    }

    private Object getValueByRules(JSON json, EtlDataProcessRule rule) {
        Object value;
        final String dataKey = rule.getSplitKeyList().get(0);
        if (dataKey.contains(SUM_EXPRESSION)
            || dataKey.contains(MAX_EXPRESSION)
            || dataKey.contains(MIN_EXPRESSION)
            || dataKey.contains(SIZE_EXPRESSION)) {
            return getNumberValue(json, dataKey);
        }

        if (dataKey.contains(CASE_EXPRESSION)) {
            final Object caseValue = getCaseValue(json, dataKey);
            return ObjectUtils.isEmpty(caseValue)
                   && !StringUtils.isEmpty(rule.getDefaultValue()) ?
                    rule.getDefaultValue() : caseValue;
        }
        value = json.getByPath(dataKey);

        final boolean hasNonValue = ObjectUtils.isEmpty(value) || NULL_STR.equals(value.toString());

        if (rule.getHasMapping()
            && !hasNonValue) {
            return getRelationMappingValue(value, rule);
        }
        if (hasNonValue && !StringUtils.isEmpty(rule.getDefaultValue())) {
            value = rule.getDefaultValue();
        }
        return value;
    }

    private Object getRelationMappingValue(Object mappingValue, EtlDataProcessRule rule) {
        final EtlDataProcessMapping mappingRelation = rule.getEtlDataProcessMapping();
        if (ObjectUtils.isEmpty(mappingRelation)) {
            return null;
        }
        if (mappingRelation.getPretreatment()) {
            return mappingRelation.getPreQueryData().get(mappingValue);
        }
        return etlDataProcessMappingMapper.queryData(mappingRelation, mappingValue);
    }

    private Object getCaseValue(JSON json, String key) {
        final int index = key.indexOf(CASE_EXPRESSION);
        final Object resultValue = json.getByPath(key.substring(0, index));
        if (ObjectUtils.isEmpty(resultValue)) {
            return resultValue;
        }
        final String caseExpressionSting = key.substring(index + CASE_EXPRESSION.length());
        String value = "";
        for (String i : caseExpressionSting.split(",")) {
            final List<String> caseExpression = Arrays.asList(i.split(":"));
            if (resultValue.toString().equals(caseExpression.get(0))) {
                value = (caseExpression.get(1));
            }
        }
        return value;
    }


    private Object getNumberValue(JSON json, String key) {
        String numberExpression = "";
        if (key.contains(SUM_EXPRESSION)) {
            numberExpression = SUM_EXPRESSION;
        }
        if (key.contains(MAX_EXPRESSION)) {
            numberExpression = MAX_EXPRESSION;
        }
        if (key.contains(MIN_EXPRESSION)) {
            numberExpression = MIN_EXPRESSION;
        }
        if (key.contains(SIZE_EXPRESSION)) {
            numberExpression = SIZE_EXPRESSION;
        }
        List<String> keys = Arrays.asList(key.split("\\" + numberExpression));
        if (keys.size() > 2) {
            throw new ResultException("表达式仅支持单层循环计算", HttpStatus.NOT_ACCEPTABLE.value());
        }
        final Object jsonArrayObject = json.getByPath(keys.get(0));
        final JSONArray jsonArray = JSONUtil.parseArray(jsonArrayObject);
        if (numberExpression.equals(SIZE_EXPRESSION)) {
            return jsonArray.size();
        }
        BigDecimal value = BigDecimal.ZERO;
        for (Object i : jsonArray) {
            final Object number = JSONUtil.parse(i).getByPath(keys.get(1));
            final BigDecimal bigDecimal = ObjectUtils.isEmpty(number) ? BigDecimal.ZERO
                    : new BigDecimal(number.toString());
            if (numberExpression.equals(SUM_EXPRESSION)) {
                value = value.add(bigDecimal);
            }
            if (numberExpression.equals(MAX_EXPRESSION)) {
                value = value.compareTo(bigDecimal) > 0 ? value : bigDecimal;
            }
            if (numberExpression.equals(MIN_EXPRESSION)) {
                value = value.compareTo(bigDecimal) < 0 ? value : bigDecimal;
            }
        }
        return value;
    }


    /**
     * 深拷贝Map
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<Integer, List<EtlDataProcessRule>> deepClone(Map<Integer, List<EtlDataProcessRule>> obj) {
        T clonedObj = null;
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(obj);
            oos.close();

            final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            clonedObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Map<Integer, List<EtlDataProcessRule>>) clonedObj;
    }


    /**
     * 验证同一执行顺序下 操作设置是否冲突
     *
     * @param sortMappings sortMappings
     */
    private void validRulesSingleTable(List<EtlDataProcessRule> sortMappings) {
        final Set<String> targetTableSet = sortMappings.stream()
                .map(EtlDataProcessRule::getTargetTable)
                .collect(Collectors.toSet());
        if (targetTableSet.size() > 1) {
            throw new ResultException("同一执行顺序不支持多表操作",
                    HttpStatus.NOT_ACCEPTABLE.value());
        }
        final Set<String> execTypeSet =
                sortMappings.stream()
                        .collect(Collectors.groupingBy(EtlDataProcessRule::getExecType))
                        .keySet();
        final Set<String> execKeySet =
                sortMappings.stream()
                        .map(EtlDataProcessRule::getExecType)
                        .collect(Collectors.toSet());
        if (execTypeSet.size() > 1) {
            throw new ResultException("同一执行顺序不支持多类型操作",
                    HttpStatus.NOT_ACCEPTABLE.value());
        }
        if (EXEC_NEED_CONDITION.contains(execKeySet.iterator().next())) {
            final List<String> compareSymbols = sortMappings.stream()
                    .filter(EtlDataProcessRule::getIsCondition)
                    .map(EtlDataProcessRule::getConditionSymbol)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(compareSymbols)) {
                throw new ResultException("更新删除操作类型必须设置条件字段",
                        HttpStatus.NOT_ACCEPTABLE.value());
            }
        }
    }
}
