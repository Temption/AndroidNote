package com.yunfeng.goodnight.view.floatview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yunfeng.baselib.communication.UMengCalc;
import com.yunfeng.baselib.image.GlideUtil;
import com.yunfeng.goodnight.R;
import com.yunfeng.goodnight.business.monitor.MusicPlayActivity;
import com.yunfeng.goodnight.business.relax.relaxervoice.vo.CurrentPlayingVo;
import com.yunfeng.goodnight.databinding.GnFloatViewBinding;

/**
 * @Description:
 * @Author: haoshuaihui
 * @CreateDate: 2020/4/29 15:11
 */
public class FloatView extends FrameLayout implements TouchProxy.OnTouchEventListener {
    private Activity mActivity;

    private FrameLayout mDecorView;
    /**
     * 手势代理
     */
    public TouchProxy mTouchProxy = new TouchProxy(this);
    private LayoutParams mFrameLayoutParams;
    private GnFloatViewBinding mBinding;
    private ObjectAnimator playAnimator;
    private int screenHeight;
    private int screenWidth;
    private int mMeasuredWidth = SizeUtils.dp2px(54);
    private int mMeasuredHeight = SizeUtils.dp2px(54);

    public FloatView(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void attach(Activity activity) {
        mActivity = activity;
        mFrameLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mDecorView = (FrameLayout) activity.getWindow().getDecorView();

        int lastPosX = FloatViewManager.getLastPosX(getContext());
        int lastPosY = FloatViewManager.getLastPosY(getContext());

        // 首次进入,设置在右下角
        if (lastPosX == 0 && lastPosY == 0) {
            lastPosX = screenWidth - mMeasuredWidth;
            lastPosY = screenHeight * 2 / 3 - mMeasuredHeight - ImmersionBar.getNavigationBarHeight(activity);
        }
        mFrameLayoutParams.leftMargin = lastPosX;
        mFrameLayoutParams.topMargin = lastPosY;
        setLayoutParams(mFrameLayoutParams);
        //java.lang.IllegalStateException - > viewgroup.addViewInner()
        // throw new IllegalStateException("The specified child already has a parent. " +
        // You must call removeView() on the child's parent first.");
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        mDecorView.addView(this);
    }

    public void detach(Activity activity) {
        try {
            mActivity = null;
            mDecorView = (FrameLayout) activity.getWindow().getDecorView();
            mDecorView.removeView(this);
        } catch (Exception e) {

        }
    }

    private void init() {
        screenHeight = ScreenUtils.getScreenHeight();
        screenWidth = ScreenUtils.getAppScreenWidth();
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.gn_float_view, this, false);
        addView(mBinding.root);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchProxy.onTouchEvent(v, event);
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new UMengCalc.Builder().eventId("60").build();
                MusicPlayActivity.start(mActivity);
            }
        });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        mFrameLayoutParams.leftMargin += dx;
        mFrameLayoutParams.topMargin += dy;
        limitBorder(mFrameLayoutParams);
        setLayoutParams(mFrameLayoutParams);
    }


    private void limitBorder(LayoutParams normalFrameLayoutParams) {
        if (normalFrameLayoutParams.topMargin <= ImmersionBar.getStatusBarHeight(mActivity)) {
            normalFrameLayoutParams.topMargin = ImmersionBar.getStatusBarHeight(mActivity);
        }
        if (normalFrameLayoutParams.leftMargin <= 0) {
            normalFrameLayoutParams.leftMargin = 0;
        }
        int maxTopMargin = screenHeight - mMeasuredHeight;
        boolean b = ImmersionBar.hasNavigationBar(mActivity);
        if (b) {
            maxTopMargin = maxTopMargin - ImmersionBar.getNavigationBarHeight(mActivity);
        }
        if (normalFrameLayoutParams.topMargin >= maxTopMargin) {
            normalFrameLayoutParams.topMargin = maxTopMargin;
        }
        int maxLeftMargin = screenWidth - mMeasuredWidth;
        if (normalFrameLayoutParams.leftMargin >= maxLeftMargin) {
            normalFrameLayoutParams.leftMargin = maxLeftMargin;
        }
    }

    @Override
    public void onUp(int x, int y) {
        //吸附
        adsorbEdge(mFrameLayoutParams);
    }


    private void adsorbEdge(LayoutParams frameLayoutParams) {
        ValueAnimator adsorbAnim = null;
        if (frameLayoutParams.leftMargin <= screenWidth / 2f - mMeasuredWidth / 2f) {
            adsorbAnim = ValueAnimator.ofInt(frameLayoutParams.leftMargin, 0);
        } else {
            adsorbAnim = ValueAnimator.ofInt(frameLayoutParams.leftMargin, screenWidth - mMeasuredWidth);
        }
        adsorbAnim.setInterpolator(new DecelerateInterpolator());
        adsorbAnim.setDuration(500);
        adsorbAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                frameLayoutParams.leftMargin = animatedValue;
                setLayoutParams(mFrameLayoutParams);
                if (animatedValue == 0 || animatedValue == screenWidth - mMeasuredWidth) {
                    FloatViewManager.saveLastPosX(getContext(), mFrameLayoutParams.leftMargin);
                    FloatViewManager.saveLastPosY(getContext(), mFrameLayoutParams.topMargin);
                }
            }
        });
        adsorbAnim.start();
    }

    @Override
    public void onDown(int x, int y) {

    }

    public boolean isAttached(Activity activity) {
        return mActivity == activity;
    }

    public void setPlayVo(CurrentPlayingVo currentPlayingVo) {
        if (currentPlayingVo != null && currentPlayingVo.getTrack() != null) {
            Track track = currentPlayingVo.getTrack();
            GlideUtil.loadAvatar(getContext(), track.getCoverUrlMiddle(), mBinding.ivPlayAnim);
            switchPlayAnim(currentPlayingVo.isPlaying());
        }
    }

    private void switchPlayAnim(boolean result) {
        if (playAnimator == null) {
            playAnimator = ObjectAnimator.ofFloat(mBinding.ivPlayAnim, "rotation", 0f, 360f);
            playAnimator.setInterpolator(new LinearInterpolator());
            playAnimator.setDuration(4000);
            playAnimator.setAutoCancel(true);
            playAnimator.setRepeatCount(ValueAnimator.INFINITE);
            if (result) {
                playAnimator.start();
            }
        } else {
            if (result) {
                if (playAnimator.isStarted()) {
                    playAnimator.resume();
                } else {
                    playAnimator.start();
                }
            } else {
                playAnimator.pause();
            }
        }
    }


}
