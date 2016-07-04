package com.fcp.freebluelibrary.error;

/**
 * 未连接
 * Created by fcp on 2016/6/4.
 */
public class UnLinkError extends Exception {

    String detailMessage = "设备未连接";

    @Override
    public String getMessage() {
        return detailMessage;
    }
}
