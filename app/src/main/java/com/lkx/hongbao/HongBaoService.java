package com.lkx.hongbao;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * 作者: LKX
 * 时间: 2018/1/25
 * 描述: 抢红包服务
 */

public class HongBaoService extends AccessibilityService {
    private static final String TAG = "lkx";
    private boolean isAppOpen; //当前页面是否为app打开的,非手动
    private static final String LUCKY_MONEY_RECEIVE_TEXT = "领取红包";
    private static final String LUCKY_MONEY_OPEN_TEXT1 = "给你发了一个红包";
    private static final String LUCKY_MONEY_OPEN_TEXT2 = "发了一个红包，金额随机";
    private static final String LUCKY_MONEY_DETAIL_TEXT = "红包详情";
    private static final String LUCKY_MONEY_NEW_TEXT = " [微信红包]";
    private static final String LUCKY_MONEY_RECEIVE_CLASSNAME = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    private static final String LUCKY_MONEY_DETAIL_CLASSNAME = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    private static final String LUCKY_MONEY_LAUNCHER_CLASSNAME = "com.tencent.mm.ui.LauncherUI";
    private static final String LUCKY_MONEY_WECHAT_CLASSNAME = "com.tencent.mm";
    private static final String ANDROID_TEXTVIEW_NAME = "android.widget.TextView";
    private String mClassName;

    @Override
    public void onInterrupt() {

    }

    //窗口变化回调
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: //通知栏改变时
                //获取到通知栏对象
                Notification notification = (Notification) event.getParcelableData();
                if (notification == null) return;
                String content = notification.tickerText.toString();
                Log.d(TAG, "通知栏信息: " + content);
                String text = content.substring(content.indexOf(":") + 1, content.length());
                if (text.startsWith(LUCKY_MONEY_NEW_TEXT)) { //当消息开头为[微信红包]时,模拟点击通知栏
                    try {
                        notification.contentIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED: //窗口切换时
                Log.d(TAG, "onAccessibilityEvent: " + event.getClassName().toString());
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: //内容改变时
                //获取根节点
                AccessibilityNodeInfo info = getRootInActiveWindow();
                mClassName = event.getClassName().toString();
                Log.d(TAG, "onAccessibilityEvent: 当前类名: " + mClassName);
                recycle(info, new infoCallBack() { //遍历根节点
                    @Override
                    public void info(final AccessibilityNodeInfo info, String text) {
                        switch (text) {
                            case LUCKY_MONEY_OPEN_TEXT1: //准备拆开红包界面
                                //查找到拆开红包的button,模拟点击
                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //防封处理,延迟执行
                                        openLuckyMoney(info);
                                    }
                                },Integer.parseInt(SpUtil.getString(getBaseContext(),"delay","0")));
                                break;
                            case LUCKY_MONEY_RECEIVE_TEXT: //检测到未拆封红包:
                                //模拟点击红包
                                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                break;
                            case LUCKY_MONEY_DETAIL_TEXT: //红包详情
                                //模拟点击返回
                                if (isAppOpen) {
                                    performGlobalAction(GLOBAL_ACTION_BACK);
                                    isAppOpen = false;
                                }
                                break;
                        }
                    }
                });
                break;
        }
    }

    //拆红包
    private void openLuckyMoney(AccessibilityNodeInfo info) {
        AccessibilityNodeInfo parent = info.getParent(); //获取上一层节点
        for (int i = 0; i < parent.getChildCount(); i++) { //循环节点,查找到拆开红包的button
            if (parent.getChild(i).getClassName().toString().equals("android.widget.Button")) {
                //查找到了领取红包的button
                if (parent.getChild(i).isEnabled()) { //红包还没有被领取,模拟点击
                    parent.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    isAppOpen = true;
                }
            }
        }
    }

    //递归根节点
    public void recycle(AccessibilityNodeInfo info, infoCallBack infoCallBack) {
        if (info != null && info.getChildCount() == 0) {
            if (info.getText() != null) {
//                Log.d(TAG, info.getText().toString());
                if (mClassName.equals(ANDROID_TEXTVIEW_NAME) && info.getText().toString().equals(LUCKY_MONEY_RECEIVE_TEXT)) {
                    Log.d(TAG, "-------找到未拆封红包-------");
                    infoCallBack.info(info, LUCKY_MONEY_RECEIVE_TEXT);
                } else if (mClassName.equals(ANDROID_TEXTVIEW_NAME) && (info.getText().toString().equals(LUCKY_MONEY_OPEN_TEXT1) || info.getText().toString().equals(LUCKY_MONEY_OPEN_TEXT2))) {
                    Log.d(TAG, "-------找到未打开红包-------");
                    infoCallBack.info(info, LUCKY_MONEY_OPEN_TEXT1);
                } else if (info.getText().toString().equals(LUCKY_MONEY_DETAIL_TEXT)) {
                    Log.d(TAG, "-------红包详情-------");
                    infoCallBack.info(info, LUCKY_MONEY_DETAIL_TEXT);
                }
            }
        } else {
            if (info != null) {
                for (int i = 0; i < info.getChildCount(); i++) {
                    recycle(info.getChild(i), infoCallBack);
                }
            }
        }
    }

    public interface infoCallBack {
        void info(AccessibilityNodeInfo info, String text);
    }
}
