

## Handler机制

[TOC]

### 基本理解

Handler构造需要Looper（子线程创建Handler异常）
Looper.prepare()将Looper绑定到当前线程（ThreadLocal）
Looper持有MessageQueue,将消息不断的取出，发送到Message的target，多个Handler可以对应一个Looper

### 源码解析

```java

   public class ActivityThread {
        public void main(String args[]) {
            //将Loopper绑定当前Thread
            Looper.prepareMainLooper();

            ActivityThread thread = new ActivityThread();
            //接受系统服务AMS发来的消息
            thread.attach(false);

            if (sMainThreadHandler == null) {
                sMainThreadHandler = thread.getHandler();
            }

            if (false) {
                Looper.myLooper().setMessageLogging(new
                        LogPrinter(Log.DEBUG, "ActivityThread"));
            }

            // End of event ActivityThreadMain.
            Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
            Looper.loop();
        }

        final void handleResumeActivity(IBinder token,
                                        boolean clearHide, boolean isForward, boolean reallyResume) {
            //1、调用activity的onResume方法，会调用到Activity的onResume方法
            ActivityClientRecord r = performResumeActivity(token, clearHide);
            //......
            if (r != null) {
                final Activity a = r.activity;
                final int forwardBit = isForward ?
                        WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION : 0;
                //.......................
                if (r.window == null && !a.mFinished && willBeVisible) {
                    r.window = r.activity.getWindow();
                    View decor = r.window.getDecorView();
                    //2、decorView先暂时隐藏
                    decor.setVisibility(View.INVISIBLE);
                    ViewManager wm = a.getWindowManager();
                    WindowManager.LayoutParams l = r.window.getAttributes();
                    a.mDecor = decor;
                    l.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
                    l.softInputMode |= forwardBit;
                    if (a.mVisibleFromClient) {
                        a.mWindowAdded = true;
                        //3、关键函数 添加到window
                        wm.addView(decor, l);
                    }
                    //..............
                    r.activity.mVisibleFromServer = true;
                    mNumVisibleActivities++;
                    if (r.activity.mVisibleFromClient) {
                        //添加decorView之后，设置可见，从而显示了activity的界面
                        r.activity.makeVisible();
                    }
                }
            }

        }
    }
```

```java
 public final class Looper {
        final Looper me = myLooper();
        final MessageQueue queue = me.mQueue;

        public static void loop() {
            for (;;) {
				// might block 重点
                Message msg = queue.next(); 
                //分发
                try {
                    msg.target.dispatchMessage(msg);
                    dispatchEnd = needEndTime ? SystemClock.uptimeMillis() : 0;
                } finally {
                    if (traceTag != 0) {
                        Trace.traceEnd(traceTag);
                    }
                }
            }
        }
    }

```

```java
	//Handler.java
	public Handler() {
        this(null, false);
    }
   /**
     * @hide  该方法被hide了，
     */
    public Handler(Callback callback, boolean async) {
         mAsynchronous = async;
    }
    //我们只能发同步消息
    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
		//异步消息在 Message 的 next过程中不受同步屏障影响
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }
}
```

### 同步屏障机制

- handleLaunchActivity 会调用onCreate方法将contentview 放到content中

```java
//ViewRootImpl.java  
//将Window和DecorView关联
setView()-->requestLayout()-->scheduleTraversals()

void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalScheduled = true;
			//1.发送同步屏障  只要发的消息处于同步屏障之后，就能获取view绘制后的信息
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();
            //2.将TraversalRunnable存放到Choreographer的“待执行队列中”
			//Choreographer收到Vsync信号时将发送异步消息（绘制任务）到主线程执行
            mChoreographer.postCallback(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
            if (!mUnbufferedInputDispatch) {
                scheduleConsumeBatchedInput();
            }
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }
	
    final class TraversalRunnable implements Runnable {
        @Override
        public void run() {
            doTraversal();
        }
    }

 void doTraversal() {
        if (mTraversalScheduled) {
            mTraversalScheduled = false;
            //移除同步屏障并执行三大流程
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
            performTraversals();
        }
  }

 private void performTraversals() {
        //1. View树的测量-可以不测量直接走布局和绘制
        if (mFirst || windowShouldResize || insetsChanged || viewVisibilityChanged || ...) {
            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            layoutRequested = true;  //需要进行布局
        }
        //2. View树的布局-可以不布局直接走绘制
        final boolean didLayout = layoutRequested && (!mStopped || mReportNextDraw);
        if (didLayout) {
            performLayout(lp, mWidth, mHeight);
        }
        //3. View树的绘制-可以不进行绘制
        boolean cancelDraw = mAttachInfo.mTreeObserver.dispatchOnPreDraw() || !isViewVisible;
        if (!cancelDraw && !newSurface) {
            performDraw();
        }
}
```

```java
public final class MessageQueue {
        private int postSyncBarrier(long when) {
            // Enqueue a new sync barrier token.
            // We don't need to wake the queue because the purpose of a barrier is to stall it.
            synchronized (this) {
                final int token = mNextBarrierToken++;
                final Message msg = Message.obtain();
                msg.markInUse();
                msg.when = when;
                msg.arg1 = token;
                //...插入消息
                return token;
            }
        }
		//这里保证了异步消息的优先级最大
        Message next() {
            //如果有同步屏障，只能返回异步消息
        }
    }
```



### IdleHandler-LeakCanary

```java
public final class MessageQueue{
     final class GcIdler implements MessageQueue.IdleHandler {
     //当messageQueue 用完所有的消息（不包括延迟消息）
        @Override
        public final boolean queueIdle() {
            doGcIfNeeded();
			//调用后是否自动移除 
            return false;	
        }
    }
        //手动移除
        public void removeIdleHandler(@NonNull IdleHandler handler) {
                synchronized (this) {
                    mIdleHandlers.remove(handler);
                }
        }
        //手动移除
        public void removeIdleHandler(@NonNull IdleHandler handler) {
                synchronized (this) {
                    mIdleHandlers.remove(handler);
                }
        }
}
```



```java
//ActivityThread 使用该IdleHandler完成Gc
final class GcIdler implements MessageQueue.IdleHandler {
     //当messageQueue 用完所有的消息（不包括延迟消息）
        @Override
        public final boolean queueIdle() {
            doGcIfNeeded();
			//调用后是否自动移除 
            return false;
        }
}
```








