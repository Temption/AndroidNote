package com.yunfeng.goodnight.view.floatview;

import android.app.Activity;
import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.chuanglan.shanyan_sdk.view.CTCCPrivacyProtocolActivity;
import com.chuanglan.shanyan_sdk.view.ShanYanOneKeyActivity;
import com.cmic.sso.sdk.activity.LoginAuthActivity;
import com.yunfeng.baselib.ActivityManager;
import com.yunfeng.goodnight.BasicApp;
import com.yunfeng.goodnight.business.login.activity.LoginActivity;
import com.yunfeng.goodnight.business.login.activity.LoginRouterActivity;
import com.yunfeng.goodnight.business.main.activity.SplashActivity;
import com.yunfeng.goodnight.business.message.activity.ChatReportActivity;
import com.yunfeng.goodnight.business.monitor.AlarmClockActivity;
import com.yunfeng.goodnight.business.monitor.AlarmClockMusicActivity;
import com.yunfeng.goodnight.business.monitor.FocusOnActivity;
import com.yunfeng.goodnight.business.monitor.MusicPlayActivity;
import com.yunfeng.goodnight.business.monitor.SleepOnActivity;
import com.yunfeng.goodnight.business.relax.relaxer.RelaxerGuideActivity;
import com.yunfeng.goodnight.business.relax.relaxervoice.vo.CurrentPlayingVo;

/**
 * @Description:
 * @Author: haoshuaihui
 * @CreateDate: 2020/4/29 15:04
 */
public class FloatViewManager {

    private FloatView floatView;
    private CurrentPlayingVo mPlayingVo;

    private FloatViewManager() {
        floatView = new FloatView(BasicApp.getInstance());
    }

    public static FloatViewManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static int getLastPosX(Context context) {
        return SPUtils.getInstance().getInt("FLOAT_ICON_POS_X", 0);
    }

    public static int getLastPosY(Context context) {
        return SPUtils.getInstance().getInt("FLOAT_ICON_POS_Y", 0);
    }

    public static void saveLastPosY(Context context, int val) {
        SPUtils.getInstance().put("FLOAT_ICON_POS_Y", val);
    }

    public static void saveLastPosX(Context context, int val) {
        SPUtils.getInstance().put("FLOAT_ICON_POS_X", val);
    }


    public void setCurrentPlaying(CurrentPlayingVo vo) {
        mPlayingVo = vo;
        Activity activity = ActivityManager.getInstance().currentActivity();
        if (activity == null) {
            return;
        }
        if (mPlayingVo != null && mPlayingVo.isPlaying()) {
            floatView.setPlayVo(vo);
            attach(activity);
        } else {
            detach(activity);
        }
    }

    public void attach(Activity activity) {
        if (activity instanceof MusicPlayActivity
                || activity instanceof SplashActivity
                || activity instanceof ChatReportActivity
                || activity instanceof LoginActivity
                || activity instanceof LoginRouterActivity
                || activity instanceof CTCCPrivacyProtocolActivity
                || activity instanceof ShanYanOneKeyActivity
                || activity instanceof LoginAuthActivity
                || activity instanceof FocusOnActivity
                || activity instanceof AlarmClockActivity
                || activity instanceof SleepOnActivity
                || activity instanceof AlarmClockMusicActivity
                || activity instanceof RelaxerGuideActivity) {
            return;
        }

        if (mPlayingVo != null && mPlayingVo.isPlaying() && !floatView.isAttached(activity)) {
            floatView.attach(activity);
        }
    }

    public void detach(Activity activity) {
        if (floatView.isAttached(activity)) {
            floatView.detach(activity);
        }
    }

    private static class SingletonHolder {
        private static final FloatViewManager INSTANCE = new FloatViewManager();
    }

}
