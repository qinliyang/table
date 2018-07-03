package net.yiersan.table.api;

import java.io.IOException;
import java.net.ConnectException;
public class TimeOutException extends IOException {

    private String requestUrl;

    public TimeOutException(Throwable throwable, String requestUrl) {
        super(throwable);
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
