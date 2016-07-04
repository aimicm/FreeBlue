package com.fcp.freebluelibrary.data;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 蓝牙数据发送类
 * Created by fcp on 2016/6/5.
 */
public interface BlueData<T> {

    void send(OutputStream outputStream, T mData);

    void receive(InputStream inputStream);


}
