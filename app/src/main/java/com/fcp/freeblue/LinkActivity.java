package com.fcp.freeblue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fcp.freebluelibrary.BlueBuilder;
import com.fcp.freebluelibrary.creator.BlueToothClient;
import com.fcp.freebluelibrary.creator.BlueToothServer;
import com.fcp.freebluelibrary.data.BlueData;
import com.fcp.freebluelibrary.data.BlueStringData;
import com.fcp.freebluelibrary.listener.OnCreateListener;
import com.fcp.freebluelibrary.listener.OnReceiveDataListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *
 * Created by fcp on 2016/6/5.
 */
public class LinkActivity extends AppCompatActivity implements OnReceiveDataListener<String> {
    public static final String TYPE = "type";
    public static final int CREATE_CLIENT = 0x101;//客户端
    public static final int CREATE_SERVER = 0x102;//服务端
    //
    public static final String ADDRESS = "address";
    //
    @InjectView(R.id.send_text)
    EditText mSendText;
    @InjectView(R.id.send_btn)
    Button mSendBtn;
    @InjectView(R.id.receive_text)
    EditText mReceiveText;
    @InjectView(R.id.break_btn)
    Button mBreakBtn;

    BlueBuilder<String> mBlueBuilder ;
    BlueData<String> mBlueData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        ButterKnife.inject(this);
        initView();
        //
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mSendText.getText().toString().trim();
                if(str.length() > 0) {
                    try {
                        mBlueBuilder.send(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mBreakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlueBuilder.close();
            }
        });
    }

    /**
     * 初始化蓝牙连接
     */
    private void initView() {
        //判别类型
        Intent intent = getIntent();
        mBlueData = new BlueStringData();
        if (intent.getExtras().getInt("type") == CREATE_CLIENT) {
            String address = intent.getExtras().getString(ADDRESS);
            mBlueBuilder = new BlueBuilder<String>(new BlueToothClient(address),mBlueData);//创建客户端
        } else if (intent.getExtras().getInt("type") == CREATE_SERVER) {
            mBlueBuilder = new BlueBuilder<String>(new BlueToothServer(),mBlueData);//创建服务端
            mBlueBuilder.enableBlueTooth();
        } else {
            finish();
            return;
        }
        mBlueBuilder.setOnCreateListener(new OnCreateListener() {
            @Override
            public void onCreateSuccess() {
                mBlueBuilder.receive();
                mBlueBuilder.launchQueue();
                Toast.makeText(LinkActivity.this,"onCreateSuccess",Toast.LENGTH_SHORT).show();
                ((BlueStringData)(mBlueData)).setDataListener(LinkActivity.this);
            }

            @Override
            public void onCreateFail(Exception cause) {
                Toast.makeText(LinkActivity.this,"onCreateFail",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        mBlueBuilder.create();
    }

    @Override
    public void onReceiverSuccess(String data) {
        mReceiveText.setText(data);
    }
}
