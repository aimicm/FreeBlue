package com.fcp.freebluelibrary.error;

/**
 * 连接失败
 * Created by fcp on 2016/6/4.
 */
public class LinkError extends Exception {

    @Override
    public String getMessage() {
        return "连接失败";
    }
}
