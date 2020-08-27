package com.example.api.service;

import com.example.api.common.util.JobInvokeUtil;
import com.example.api.common.util.SpringUtils;
import com.example.api.dao.EtlLoadMapper;
import com.example.api.model.Conditions;
import com.example.api.model.EtlDataProcessRule;
import com.example.api.model.TableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.api.common.Constants.*;


/**
 * 转换后数据加载服务
 *
 * @author lizhuo
 */
@Service
public class EtlLoadService {

    private static final Logger logger = LoggerFactory.getLogger(EtlLoadService.class);

    @Resource
    private EtlLoadMapper etlLoadMapper;

    public void loadData(TableData tableData, List<EtlDataProcessRule> mappingSettings) {
        final String execType = mappingSettings.get(0).getExecType();
        final String tableName = tableData.getTableName();
        final List<Map<String, Object>> dataList = tableData.getData();

        if (EXEC_INSERT_REPLACE.contains(execType)) {
            etlLoadMapper.insertOrReplace(execType, tableName, dataList, dataList.get(0));
            return;
        }
        List<Conditions> conditionsList = getConditionsList(mappingSettings);
        if (EXEC_UPDATE.equals(execType)) {
            execUpdate(tableName, dataList, conditionsList);
            return;
        }
        if (EXEC_DELETE.equals(execType)) {
            execDelete(tableName, dataList, conditionsList);
            return;
        }
        if (EXEC_INSERT_OR_UPDATE.equals(execType)) {
            execInsertOrUpdate(tableName, dataList, conditionsList);
            return;
        }
        if (EXEC_JAVA_BEAN.equals(execType)) {
            execJavaBean(tableName, dataList, conditionsList);
        }
    }

    private List<Conditions> getConditionsList(List<EtlDataProcessRule> mappingSettings) {
        List<Conditions> conditionsList = new ArrayList<>(mappingSettings.size());
        mappingSettings.stream()
                .filter(EtlDataProcessRule::getIsCondition)
                .forEach(i -> conditionsList.add(new Conditions(i.getTargetColumns(), i.getConditionSymbol())));
        return conditionsList;
    }

    /**
     * 执行更新
     */
    private void execUpdate(String tableName,
                            List<Map<String, Object>> dataList,
                            List<Conditions> conditionsList) {
        dataList.forEach(data -> {
            setConditionsValue(data, conditionsList);
            etlLoadMapper.update(tableName, data, conditionsList);
        });
    }

    /**
     * 执行删除
     */
    private void execDelete(String tableName,
                            List<Map<String, Object>> dataList,
                            List<Conditions> conditionsList) {
        final String conditionsColumns = conditionsList.get(0).getColumns();
        if (conditionsList.size() == 1
            && CONDITION_IN.contains(conditionsList.get(0).getSymbol())) {

            etlLoadMapper.deleteInBatch(
                    tableName, conditionsColumns,
                    getSingleConditionsValues(dataList, conditionsColumns));
            return;
        }
        List<List<Conditions>> conditionsListBatch = getConditionsListBatch(dataList, conditionsList);
        etlLoadMapper.deleteOrBatch(tableName, conditionsListBatch);
    }


    /**
     * 执行新增或更新
     */
    private void execInsertOrUpdate(String tableName,
                                    List<Map<String, Object>> dataList,
                                    List<Conditions> conditionsList) {

        Map<Boolean, List<Map<String, Object>>> existsGroup;
        if (conditionsList.size() == 1
            && CONDITION_IN.contains(conditionsList.get(0).getSymbol())) {
            final String conditionsColumns = conditionsList.get(0).getColumns();
            List<Object> existsDataList = etlLoadMapper.selectByConditionIn(
                    tableName, conditionsColumns,
                    getSingleConditionsValues(dataList, conditionsColumns));
            existsGroup = dataList.stream()
                    .collect(Collectors.groupingBy(
                            i -> existsDataList.contains(i.get(conditionsColumns))));
        } else {
            List<Map<String, Object>> existsDataList =
                    etlLoadMapper.selectByConditionOr(
                            tableName, getConditionsListBatch(dataList, conditionsList));

            existsGroup = dataList.stream()
                    .collect(Collectors.groupingBy(data -> isExists(data, existsDataList)));
        }

        if (existsGroup.containsKey(false)
                && !CollectionUtils.isEmpty(existsGroup.get(false))) {
            final List<Map<String, Object>> insertData = existsGroup.get(false);
            etlLoadMapper.insertOrReplace(EXEC_INSERT, tableName, insertData, insertData.get(0));
        }
        if (existsGroup.containsKey(true)
                && !CollectionUtils.isEmpty(existsGroup.get(true))) {
            execUpdate(tableName, existsGroup.get(true), conditionsList);
        }
    }


    /**
     * 执行指定java method
     *
     * @param tableName      BeanName.methodName
     * @param dataList       param list
     * @param conditionsList param type
     */
    private void execJavaBean(String tableName, List<Map<String, Object>> dataList, List<Conditions> conditionsList) {
        final String[] split = tableName.split("\\.");
        dataList.forEach(i -> {
            try {
                setConditionsValue(i, conditionsList);
                invokeMethod(split[0], split[1], conditionsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void invokeMethod(String beanName, String methodName, List<Conditions> conditionsList) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object bean;
        if (!JobInvokeUtil.isValidClassName(beanName)) {
            bean = SpringUtils.getBean(beanName);
        } else {
            bean = Class.forName(beanName).newInstance();
        }
        final List<Object[]> methodParamsObject = getMethodParams(conditionsList);
        Method method = bean.getClass().getDeclaredMethod(methodName, JobInvokeUtil.getMethodParamsType(methodParamsObject));
        method.invoke(bean, JobInvokeUtil.getMethodParamsValue(methodParamsObject));

    }

    public static List<Object[]> getMethodParams(List<Conditions> conditionsList) {
        List<Object[]> classs = new LinkedList<>();
            for (Conditions conditions : conditionsList) {
                final String paramValue = conditions.getValue().toString();
                if (CONDITION_INTEGER.equals(conditions.getSymbol())) {
                    classs.add(new Object[]{Integer.valueOf(paramValue), Integer.class});
                }
                if (CONDITION_STRING.equals(conditions.getSymbol())) {
                    classs.add(new Object[]{paramValue, String.class});
                }

            }
        return classs;
    }


    private void setConditionsValue(Map<String, Object> data, List<Conditions> conditionsList) {
        conditionsList.forEach(conditions -> {
            conditions.setValue(data.get(conditions.getColumns()));
            data.remove(conditions.getColumns());
        });
    }

    /**
     * 获取单条件值集合
     *
     * @param dataList          待处理值集合
     * @param conditionsColumns 条件字段
     * @return 条件值集合
     */
    private List<Object> getSingleConditionsValues(List<Map<String, Object>> dataList,
                                                   String conditionsColumns) {
        return dataList
                .stream()
                .filter(i -> !ObjectUtils.isEmpty(i.get(conditionsColumns)))
                .map(i -> i.get(conditionsColumns))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取多条件带值集合
     *
     * @param dataList       待处理值集合
     * @param conditionsList 条件
     * @return 条件带值集合
     */
    private List<List<Conditions>> getConditionsListBatch(List<Map<String, Object>> dataList,
                                                          List<Conditions> conditionsList) {
        List<List<Conditions>> conditionsListBatch = new ArrayList<>(dataList.size());
        dataList.forEach(i -> {
            conditionsList.forEach(j -> j.setValue(i.get(j.getColumns())));
            conditionsListBatch.add(deepCopyList(conditionsList));
        });
        return conditionsListBatch;
    }

    /**
     * 判断待处理值是否已存在
     *
     * @param data           待处理值
     * @param existsDataList 已存在值集合
     * @return 是否已存在
     */
    private Boolean isExists(Map<String, Object> data, List<Map<String, Object>> existsDataList) {
        return existsDataList.stream().anyMatch(existsData -> mapEquals(data, existsData));
    }

    /**
     * 判断已存在值与待处理值是否相同
     *
     * @param data      待处理值
     * @param existsMap 已存在值
     * @return 是否相同
     */
    private boolean mapEquals(Map<String, Object> data, Map<String, Object> existsMap) {
        //避免类型不一致造成比对失效
        Map<String, Object> conditionsData = new HashMap<>(existsMap.size());
        Map<String, Object> existsMapConvert = new HashMap<>(existsMap.size());

        existsMap.forEach((key, value) -> {
            conditionsData.put(key, data.get(key).toString());
            existsMapConvert.put(key, String.valueOf(value));
        });
        return conditionsData.equals(existsMapConvert);
    }


    public static <T> List<T> deepCopyList(List<T> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (Exception e) {
            logger.info(e.toString());
            return Collections.emptyList();
        }

    }

    public Object execSqlSignValue(String sql) {
        return etlLoadMapper.execSqlSignValue(sql);
    }

    public List<Object> execSqlListValue(String sql) {
        return etlLoadMapper.execSqlListValue(sql);
    }

    public void execSqlVoid(String sql) {
        etlLoadMapper.execSqlVoid(sql);
    }


}
