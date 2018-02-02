## Android微信红包辅助 ##
### 马上过年了,又到了红包满天飞的季节,emmmm...先来看效果图 ###
![Alt text](https://github.com/lvkaixuan/HongBao/blob/master/QQ20180202-163554.gif)
### 实现思路 ###
 - 使用AccessibilityService监听红包消息
 - 当有红包的时候模拟点击
### 核心代码 ###

```
case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: //内容改变时
    //获取根节点
    AccessibilityNodeInfo info = getRootInActiveWindow();
    mClassName = event.getClassName().toString();
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
                    },Integer.parseInt
                        (SpUtil.getString(getBaseContext(),"delay","0")));
                    break;
                case LUCKY_MONEY_RECEIVE_TEXT: //检测到未拆封红包:
                    //模拟点击红包
                    info.getParent()
                        .performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
```

### 项目 ###

 - GitHub地址: https://github.com/lvkaixuan/HongBao
 - Demo下载: https://fir.im/wechathongbao
 - 喜欢的话请star一下,支持开源
 - logo来自阿里图标,侵权删
 - 扫码下载apk
 ![这里写图片描述](https://github.com/lvkaixuan/HongBao/blob/master/scan_download.png)
 
