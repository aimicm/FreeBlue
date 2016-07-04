package com.fcp.freebluelibrary.error;

/**
 * 服务端失败
 * Created by fcp on 2016/6/4.
 */
public class ServerError extends Exception{

    @Override
    public String getMessage() {
        return "建立服务器失败";
    }
}
