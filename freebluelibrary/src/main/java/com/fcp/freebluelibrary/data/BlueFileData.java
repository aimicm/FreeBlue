package com.fcp.freebluelibrary.data;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.fcp.freebluelibrary.listener.OnFileEndListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件操作
 * Created by fcp on 2016/6/6.
 */
public class BlueFileData extends BlueBaseData<File> {
    private static final int PROGRESS = 0;
    private static final int SEND_FINISH = 1;
    private static final int RECEIVE_FINISH = 2;
    private static final int ERROR = 3;


    File receiveFile = Environment.getExternalStorageDirectory();//接收的文件的存放文件夹
    FileOutputStream mFileOutputStream;

    byte[] buffer = new byte[2048]; // buffer store for the stream
    int bytes; // bytes returned from read()

    OnFileEndListener mOnFileEndListener;



    public void setReceiveFile(File receiveFile){
        this.receiveFile = receiveFile;
    }

    @Override
    public void send(OutputStream outputStream, File mData) {
        FileInputStream in = null;
        DataOutputStream mDataOutputStream = new DataOutputStream(outputStream);
        try {
            in = new FileInputStream(mData);
            int c;
            byte[] buffer = new byte[2048];
            mDataOutputStream.writeBytes("START\r\n");
            mDataOutputStream.writeUTF(mData.getName());// 名字
            mDataOutputStream.writeLong(mData.length());// 大小
            //文件本体
            while((c = in.read(buffer))!=-1){
                mDataOutputStream.write(buffer, 0, c);
                mDataOutputStream.flush();
            }
            mResultHandler.sendEmptyMessage(SEND_FINISH);
        } catch (Exception e) {
            e.printStackTrace();
            mResultHandler.sendMessage(mResultHandler.obtainMessage(ERROR,e));
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    boolean findFile = false;
    long fileSize = Long.MAX_VALUE;
    long hasRead = 0;

    @Override
    public void receive(InputStream inputStream) {
        // Read from the InputStream
        try {
            bytes = inputStream.read(buffer);
            if(!findFile){
                if(bytes == Byte.valueOf("START\r\n")){
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    File mFile = new File(receiveFile+File.separator+dataInputStream.readUTF());
                    fileSize = dataInputStream.readLong();
                    mFileOutputStream = new FileOutputStream(mFile);
                }
            }else {
                if(bytes != -1) {
                    mFileOutputStream.write(buffer,0,bytes);
                    hasRead += bytes;
                    mResultHandler.sendMessage(mResultHandler.obtainMessage(PROGRESS,(int)hasRead/fileSize));
                }else {
                    mFileOutputStream.close();
                    mResultHandler.sendEmptyMessage(RECEIVE_FINISH);
                    findFile = false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            mResultHandler.sendMessage(mResultHandler.obtainMessage(ERROR,e));
        }
    }

    public void setOnFileEndListener(OnFileEndListener onFileEndListener) {
        mOnFileEndListener = onFileEndListener;
    }

    @Override
    protected void getMessage(Message msg) {
        if(mOnFileEndListener!=null){
            switch (msg.what){
                case PROGRESS:
                    mOnFileEndListener.onProgress((int)msg.obj);
                    break;
                case SEND_FINISH:
                    mOnFileEndListener.onSendFinish();
                    break;
                case RECEIVE_FINISH:
                    mOnFileEndListener.onReceiveFinish();
                    break;
                case ERROR:
                    mOnFileEndListener.onError((Exception) msg.obj);
                    break;
            }
        }
    }
}
