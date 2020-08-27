package com.example.api.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.api.dao.EtlSourceDataMapper;
import com.example.api.dao.EtlSourceMapper;
import com.example.api.exception.ResultException;
import com.example.api.model.EtlSource;
import com.example.api.model.EtlSourceData;
import com.example.api.model.EtlSourceValid;
import com.sun.istack.internal.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.example.api.common.Constants.*;


/**
 * 源数据萃取服务
 *
 * @author lizhuo
 */
@Component("etlExtractService")
public class EtlExtractService {

    @Resource
    private EtlSourceMapper etlSourceMapper;

    @Resource
    private EtlProcessService etlProcessService;
    @Resource
    private EtlSourceDataMapper etlSourceDataMapper;

    public void executorClient(@NotNull String sourceKey,
                               @NotNull Map<String, Object> param,
                               Map<String, String> header) {
        final EtlSource etlSource = etlSourceMapper.selectEtlSourceByKey(sourceKey);
        if (ObjectUtils.isEmpty(etlSource)) {
            throw new ResultException("接口配置信息不存在", HttpStatus.NOT_ACCEPTABLE.value());
        }
        final JSONObject sourceData = exchangeBase(etlSource, param, header);
        validSourceData(etlSource.getId(), sourceData);

        final Integer resultProcessMethod = etlSource.getProcessMethod();
        etlSourceDataMapper.insert(new EtlSourceData(sourceKey, sourceData.toString(),
                PROCESSING_STATUS_UNPROCESSED));

        if (PROCESS_METHOD_SYNC.equals(resultProcessMethod)) {
            etlProcessService.processSourceData(sourceKey);
        }

        if (PROCESS_METHOD_ASYNC.equals(resultProcessMethod)) {
            CompletableFuture
                    .runAsync(() -> etlProcessService.processSourceData(sourceKey));
        }
    }

    /**
     * 验证接口返回业务状态码
     */
    private void validSourceData(Integer etlSourceId, JSONObject result) {
        final EtlSourceValid etlSourceValid = etlSourceMapper.selectEtlSourceValidBySourceId(etlSourceId);
        if (ObjectUtils.isEmpty(etlSourceValid)
                || StringUtils.isEmpty(etlSourceValid.getSuccessCode())) {
            return;
        }
        if (result.get(etlSourceValid.getCodeKey()).toString()
                .equals(etlSourceValid.getSuccessCode())) {
            return;
        }
        throw new ResultException(
                result.getStr(etlSourceValid.getMsgKey()),
                result.getInt(etlSourceValid.getCodeKey()));
    }

    /**
     * 发起请求
     *
     * @param etlSource 客户端信息
     * @param param     参数
     * @param header    头信息
     * @return the result
     */
    private JSONObject exchangeBase(EtlSource etlSource,
                                    Map<String, Object> param,
                                    Map<String, String> header) {
        if (CONTENT_TYPE_FORM == etlSource.getContentType()) {
            String result = new HttpRequest(etlSource.getSourceUrl())
                    .method(Method.valueOf(etlSource.getRequestMethod()))
                    .form(param)
                    .execute().body();
            return JSONUtil.parseObj(result);
        } else if (CONTENT_TYPE_JSON == etlSource.getContentType()) {
            String result = new HttpRequest(etlSource.getSourceUrl())
                    .method(Method.valueOf(etlSource.getRequestMethod()))
                    .addHeaders(header)
                    .body(JSONUtil.parse(param).toString())
                    .execute().body();
            return JSONUtil.parseObj(result);
        }
        throw new ResultException("接口请求类型未知", HttpStatus.NOT_ACCEPTABLE.value());
    }


}
