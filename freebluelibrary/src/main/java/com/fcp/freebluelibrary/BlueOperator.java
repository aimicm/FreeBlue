package com.fcp.freebluelibrary;

/**
 * 蓝牙操作
 * Created by fcp on 2016/6/5.
 */
public interface BlueOperator<T> {

    /**
     * 生成：生成服务端或客户端
     */
    void create();

    /**
     * 发送数据
     */
    void send(T mData);

    /**
     * 开启接收
     */
    void receive();

    /**
     * 关闭资源
     */
    void close();



}
