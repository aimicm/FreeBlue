package com.fcp.freebluelibrary.listener;

/**
 * 接收数据接口
 * Created by fcp on 2015/9/7.
 */
public interface OnReceiveDataListener<T> {
    /**
     * 成功获取到数据
     * @param data 数据
     */
    void onReceiverSuccess(T data);
}
