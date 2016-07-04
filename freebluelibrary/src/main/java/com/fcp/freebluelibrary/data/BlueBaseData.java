package com.fcp.freebluelibrary.data;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

/**
 * 数据基类
 * Created by fcp on 2016/6/6.
 */
public abstract class BlueBaseData<T> implements BlueData<T> {

    ResultHandler mResultHandler;

    public BlueBaseData() {
        mResultHandler = new ResultHandler();
    }

    @SuppressLint("HandlerLeak")
    class ResultHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            getMessage(msg);
        }
    }

    protected abstract void getMessage(Message msg);

}
