package com.jaydenxiao.common.basebean;

import java.io.Serializable;

/**
 */
public class BaseRespose <T> implements Serializable {
    public String code;
    public String msg;

    public T data;

    public boolean success() {
        return "1".equals(code);
    }

    @Override
    public String toString() {
        return "BaseRespose{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
