package com.fcp.freeblue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fcp.freebluelibrary.BlueBuilder;
import com.fcp.freebluelibrary.creator.BlueToothClient;
import com.fcp.freebluelibrary.creator.BlueToothServer;
import com.fcp.freebluelibrary.data.BlueData;
import com.fcp.freebluelibrary.data.BlueFileData;
import com.fcp.freebluelibrary.data.BlueStringData;
import com.fcp.freebluelibrary.listener.OnCreateListener;
import com.fcp.freebluelibrary.listener.OnFileEndListener;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 文件传输
 * Created by fcp on 2016/6/6.
 */
public class FileActivity extends AppCompatActivity {

    public static final String TYPE = "type";
    public static final int CREATE_CLIENT = 0x101;//客户端
    public static final int CREATE_SERVER = 0x102;//服务端
    //
    public static final String ADDRESS = "address";
    //
    BlueBuilder<File> mBlueBuilder;
    BlueFileData mBlueData;
    //
    @InjectView(R.id.send_file_btn)
    Button mSendFileBtn;
    @InjectView(R.id.send_file_bar)
    ProgressBar mSendFileBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ButterKnife.inject(this);
        initView();
        //测试文件
        final String sdPath = Environment.getExternalStorageDirectory()+"/Download/tzb.zip";
        //
        mSendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlueBuilder.send(new File(sdPath));
            }
        });
    }

    /**
     * 初始化蓝牙连接
     */
    private void initView() {
        //判别类型
        Intent intent = getIntent();
        mBlueData = new BlueFileData();
        mBlueData.setReceiveFile(Environment.getExternalStorageDirectory());
        if (intent.getExtras().getInt("type") == CREATE_CLIENT) {
            String address = intent.getExtras().getString(ADDRESS);
            mBlueBuilder = new BlueBuilder<File>(new BlueToothClient(address), mBlueData);//创建客户端
        } else if (intent.getExtras().getInt("type") == CREATE_SERVER) {
            mBlueBuilder = new BlueBuilder<File>(new BlueToothServer(), mBlueData);//创建服务端
            mBlueBuilder.enableBlueTooth();
        } else {
            finish();
            return;
        }
        mBlueBuilder.setOnCreateListener(new OnCreateListener() {
            @Override
            public void onCreateSuccess() {
                mBlueBuilder.receive();
                Toast.makeText(FileActivity.this, "onCreateSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCreateFail(Exception cause) {
                Toast.makeText(FileActivity.this, "onCreateFail", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        mBlueBuilder.create();
        mBlueData.setOnFileEndListener(new OnFileEndListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onReceiveFinish() {
                Toast.makeText(FileActivity.this, "onReceiveFinish", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSendFinish() {
                Toast.makeText(FileActivity.this, "onSendFinish", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception mException) {
                Toast.makeText(FileActivity.this, "onError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlueBuilder.close();
    }
}
