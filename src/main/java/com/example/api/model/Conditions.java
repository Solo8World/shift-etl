package com.example.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lizhuo
 */

@Data
public class Conditions implements Serializable {
    private static final long serialVersionUID = 1L;

    private String columns;
    private String symbol;
    private Object value;

    public Conditions(String columns, String symbol) {
        this.columns = columns;
        this.symbol = symbol;
    }
}
