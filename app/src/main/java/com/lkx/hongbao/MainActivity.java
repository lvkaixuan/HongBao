package com.lkx.hongbao;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Switch mServiceSwitch;
    private SeekBar mSeekBar;
    private TextView mDelay;
    private LinearLayout mGithub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServiceSwitch = (Switch) findViewById(R.id.serviceSwitch);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mDelay = (TextView) findViewById(R.id.delay);
        mGithub = (LinearLayout) findViewById(R.id.github);
        initSwitch();
        initSeekBar();
        initGitHub();
    }

    private void initGitHub() {
        mGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });
    }

    private void initSeekBar() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDelay.setText(progress+"ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "当有新的红包出现时,将会延迟" + seekBar.getProgress() + "ms后拆开", Toast.LENGTH_LONG).show();
                SpUtil.saveString(MainActivity.this,"delay",String.valueOf(seekBar.getProgress()));
            }
        });
    }

    private void initSwitch() {
        if (isStartAccessibilityService(this, getPackageName()+"/.HongBaoService")) {
            mServiceSwitch.setChecked(true);
        }
        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //跳转至无障碍界面
                Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(accessibleIntent);
                Toast.makeText(MainActivity.this, "点击微信红包辅助->"+(isChecked?"开启":"关闭")+"即可", Toast.LENGTH_LONG).show();
            }
        });
    }

    //判断服务是否正在运行
    public static boolean isStartAccessibilityService(Context context, String name){
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            if (id.contains(name)) {
                return true;
            }
        }
        return false;
    }
}
