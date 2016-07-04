package com.fcp.freebluelibrary.listener;

/**
 * 文件传输结束
 * Created by fcp on 2016/6/6.
 */
public interface OnFileEndListener {
    void onProgress(int progress);

    void onReceiveFinish();

    void onSendFinish();

    void onError(Exception mException);

}
