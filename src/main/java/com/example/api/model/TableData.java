package com.example.api.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lizhuo
 */
@Data
public class TableData {
    private String tableName;
    private List<Map<String, Object>> data;

    public TableData(String tableName, List<Map<String, Object>> data) {
        this.tableName = tableName;
        this.data = data;
    }
}