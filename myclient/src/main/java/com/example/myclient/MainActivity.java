package com.example.myclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myclient.netty.RequestUtil;
import com.example.myclient.utils.IPUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "cj";
    private Button bt1;
    private TextView tv_ip;
    private EditText et_ip;
    private EditText et_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        tv_ip.setText(IPUtils.getIp());
    }

    private void initView() {
        bt1 = findViewById(R.id.bt1);
        tv_ip = findViewById(R.id.tv_ip);
        et_ip = findViewById(R.id.et_ip);
        et_port = findViewById(R.id.et_port);

        bt1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:

                submit();

//
                break;
        }
    }


    private void submit() {
        // validate
        String ip = et_ip.getText().toString().trim();
        if (TextUtils.isEmpty(ip)) {
            Toast.makeText(this, "ip不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String port = et_port.getText().toString().trim();
        if (TextUtils.isEmpty(port)) {
            Toast.makeText(this, "port不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        RequestUtil.post(ip, Integer.parseInt(port));

    }
}
