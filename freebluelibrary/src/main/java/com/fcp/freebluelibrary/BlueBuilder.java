package com.fcp.freebluelibrary;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.fcp.freebluelibrary.creator.BlueCreator;
import com.fcp.freebluelibrary.data.BlueData;
import com.fcp.freebluelibrary.error.CreateError;
import com.fcp.freebluelibrary.listener.OnCreateListener;
import com.fcp.freebluelibrary.queue.ChargeQueue;

import java.lang.ref.WeakReference;

/**
 * 蓝牙合成类
 * Created by fcp on 2016/6/5.
 */
public class BlueBuilder<T> implements BlueOperator<T>{
    protected final String TAG = this.getClass().getSimpleName();
    /**
     * 是否连接
     */
    protected boolean isConnection = false;
    /**
     * 适配器
     */
    protected BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    /**
     * create线程处理类
     */
    protected CreateHandler createHandler = new CreateHandler(this);
    /**
     * 生成监听器
     */
    protected OnCreateListener onCreateListener;
    /**
     * 蓝牙创建者
     */
    private BlueCreator mBlueCreator;
    /**
     * 数据者
     */
    private BlueData<T> mBlueData;

    public BlueBuilder(BlueCreator mBlueCreator,BlueData<T> blueData) {
        this.mBlueData = blueData;
        this.mBlueCreator = mBlueCreator;
    }

    //=========================================创建=====================================================
    @Override
    public void create() {
        if(isConnection){
            //已经连接
            createFail(new CreateError("BlueTooth has created"));
        }else if(mBlueCreator == null){
            createFail(new CreateError("mBlueCreator can not be null"));
        }else{
            if(this.bluetoothAdapter.isDiscovering()){//断开搜索
                this.bluetoothAdapter.cancelDiscovery();
            }
            new CreatorThread().start();//开启线程
        }
    }

    /**
     * 生成服务器线程
     */
    class CreatorThread extends Thread {
        @Override
        public void run() {
            Message message = createHandler.obtainMessage();
            message.obj = mBlueCreator.create();
            createHandler.sendMessage(message);
        }
    }

    /**
     * 处理线程返回值
     */
    public static class CreateHandler extends Handler {
        WeakReference<BlueBuilder> weakReference;
        public CreateHandler(BlueBuilder mBlueBaseWorker) {
            weakReference=new WeakReference<>(mBlueBaseWorker);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BlueBuilder mBlueBaseWorker = weakReference.get();
            if(mBlueBaseWorker!=null){
                if((boolean)(msg.obj)){
                    mBlueBaseWorker.createSuccess();
                }else{
                    mBlueBaseWorker.createFail(new CreateError("message转换类型异常"));
                }
            }
        }
    }

    public void createSuccess() {
        isConnection=true;
        if(onCreateListener!=null)onCreateListener.onCreateSuccess();
    }

    protected void createFail(CreateError mCreateError) {
        if(onCreateListener!=null)onCreateListener.onCreateFail(mCreateError);
    }

    public void setOnCreateListener(OnCreateListener onCreateListener) {
        this.onCreateListener = onCreateListener;
    }

    //=========================================发送=====================================================

    ChargeQueue<T> mChargeQueue;
    /**
     * 开启队列
     */
    public void launchQueue(){
        mChargeQueue = new ChargeQueue<T>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                T mData;
                while (isConnection){
                    try {
                        mData = mChargeQueue.take();
                    } catch (InterruptedException e) {
                        if(!isConnection){
                            return;
                        }
                        continue;
                    }
                    //执行
                    mBlueData.send(mBlueCreator.getOutputStream(),mData);
                }
            }
        }).start();
    }

    @Override
    public void send(final T mData) {
        if(mChargeQueue == null){
            throw new RuntimeException("mChargeQueue is null");
        }else {
            mChargeQueue.add(mData);
        }
    }


    //=========================================接收=====================================================

    @Override
    public void receive() {
        new ReceiverThread().start();
    }

    class ReceiverThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            // Keep listening to the InputStream until an exception occurs
            while (isConnection) {
                mBlueData.receive(mBlueCreator.getInputStream());
            }
            Looper.loop();
        }
    }

    //=========================================释放=====================================================

    @Override
    public void close() {
        isConnection = false;
        mBlueCreator.release();
    }

    //
    public void enableBlueTooth(){
        bluetoothAdapter.enable();
    }


}
