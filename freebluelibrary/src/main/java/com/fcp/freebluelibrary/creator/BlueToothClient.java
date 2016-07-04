package com.fcp.freebluelibrary.creator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 蓝牙客户端
 * Created by fcp on 2016/6/5.
 */
public class BlueToothClient implements BlueCreator {

    //远程目标蓝牙地址
    private String address;
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

    /**
     * 构造方法
     * @param address 欲连接的远程连地址
     */
    public BlueToothClient(String address) {
        this.address = address;
    }

    @Override
    public boolean create() {
        try {
            //远程蓝牙设备
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            //获得通道
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
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
