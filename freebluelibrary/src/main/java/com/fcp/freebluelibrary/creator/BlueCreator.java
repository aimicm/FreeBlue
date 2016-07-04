package com.fcp.freebluelibrary.creator;


import java.io.InputStream;
import java.io.OutputStream;

/**
 * 创建类
 * Created by fcp on 2016/6/5.
 */
public interface BlueCreator {

    /**
     * 创建
     */
    boolean create();

    /**
     * 获得输入通道
     */
    InputStream getInputStream();


    /**
     * 获得输出通道
     */
    OutputStream getOutputStream();


    /**
     * 释放
     */
    void release();

}
