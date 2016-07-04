package com.fcp.freebluelibrary.data;

import android.os.Message;

import com.fcp.freebluelibrary.listener.OnReceiveDataListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 字符串
 * Created by fcp on 2016/6/5.
 */
public class BlueStringData extends BlueBaseData<String> {
    private static final String DATA_FORM = "UTF-8";//发送数据格式
    public static final int BUFFER_LENGTH = 1024;//单次传输量
    byte[] buffer = new byte[BUFFER_LENGTH]; // buffer store for the stream
    int bytes; // bytes returned from read()
    OnReceiveDataListener<String> mDataListener = new OnReceiveDataListener<String>() {
        @Override
        public void onReceiverSuccess(String data) {
            //do nothing
        }
    };



    @Override
    public void send(OutputStream outputStream, String mData) {
        try {
            byte[] data = mData.getBytes(DATA_FORM);
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(InputStream inputStream) {
        try {
            // Read from the InputStream
            bytes = inputStream.read(buffer);
            Message message = mResultHandler.obtainMessage();
            message.obj = buffer;
            message.arg1 = 0;
            message.arg2 = bytes;
            mResultHandler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDataListener(OnReceiveDataListener<String> dataListener) {
        mDataListener = dataListener;
    }

    @Override
    protected void getMessage(Message msg) {
        mDataListener.onReceiverSuccess(new String((byte[]) msg.obj, msg.arg1, msg.arg2));
    }
}
