package com.example.myserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myserver.netty.ServerUtil;
import com.example.myserver.utils.IPUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_ip;
    private Button bt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        tv_ip.setText(IPUtils.getIp());
    }

    private void initView() {
        tv_ip = (TextView) findViewById(R.id.tv_ip);
        bt1 = (Button) findViewById(R.id.bt1);

        bt1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:

                ServerUtil.start();

                break;
        }
    }


}
