package com.fcp.freeblue;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createServer(View mView){
        //打开界面
        Intent intent = new Intent(MainActivity.this,LinkActivity.class);
        intent.putExtra(LinkActivity.TYPE, LinkActivity.CREATE_SERVER);
        startActivity(intent);
    }

    public void findServer(View mView){
        //打开搜索界面
        startActivity(new Intent(MainActivity.this,FindActivity.class));
    }

}
