package com.fcp.freebluelibrary.creator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 蓝牙服务端
 * Created by fcp on 2016/6/5.
 */
public class BlueToothServer implements BlueCreator {

    /**
     * uuid for SDP record or service record uuid to lookup RFCOMM channel
     */
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /**
     * 端口
     */
    protected BluetoothSocket socket ;
    /**
     * 发送通道
     */
    protected OutputStream outputStream = null;
    /**
     * 接收通道
     */
    protected InputStream inputStream = null;
    /**
     * 适配器
     */
    protected BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



    @Override
    public boolean create() {
        boolean result = false;
        BluetoothServerSocket serverSocket = null;
        try {
            // 要建立一个ServerSocket对象，需要使用adapter.listenUsingRfcommWithServiceRecord方法
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("myServerSocket", uuid);
            socket = serverSocket.accept(10000);//超时限10s
            //获得通道
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            //result
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void release() {
        try {
            if(inputStream!=null) {
                inputStream.close();
            }
            if(outputStream!=null){
                outputStream.close();
            }
            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
