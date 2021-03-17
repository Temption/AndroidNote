## Handler机制

### Handle定义：  

andriod提供给我们更新UI 必须使用 的一套机制，我们可以发送与一个线程的MessageQueue相关联的

### 源码解析

```java

public class ActivityThread{
    public void main(String args[]){
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
   
 } 		
```

本质上我们UI的展示、更新，是通过 MainThreadHandler 来处理,因此不会卡死

ThreadLocal ：存储variable for which each thread has its own value.


```java
private static void prepare(boolean quitAllowed) {
  if (sThreadLocal.get() != null) {
    throw new RuntimeException("Only one Looper may be created per thread");
  }
	//线程
  sThreadLocal.set(new Looper(quitAllowed));
}
 public static @Nullable Looper myLooper() {
        return sThreadLocal.get();
    }
//Looper关联一个队列 与 当前线程     
 private Looper(boolean quitAllowed) {
        // 创建MessageQueue对象
        mQueue = new MessageQueue(quitAllowed);
        // 记录当前线程
        mThread = Thread.currentThread();
   }
 public static @Nullable Looper myLooper() {
        return sThreadLocal.get();
    }

  public static void loop() {
         // 获取TLS存储的Looper对象
        final Looper me = myLooper();
    	MessageQueue queue = me.mQueue;
		
	 for (;;) {
                //取出
            Message msg = queue.next(); //might block
                 msg.target.dispatchMessage(msg);//分发
     }
  }
```

### Handler

```java
//Handler持有Looper，Looper持有MessageQueue与当前Thread
public Handler(Callback callback, boolean async) {
     	//如果是匿名类，warn开发者
        //获取当前线程的Looper
        mLooper = Looper.myLooper();
        //当前线程没有Looper，抛异常
        if (mLooper == null) {
            throw new RuntimeException(
					//子线程没有looper对象
                "Can't create handler inside thread that has not called Looper.prepare()");
        }

        mQueue = mLooper.mQueue;
       
        mCallback = callback;
    		
        mAsynchronous = async;
    }


//根据when将消息插入队列
boolean enqueueMessage(Message msg, long when) {}
```



### 同步屏障机制

```java
// 从默认Handler的构造下手
//默认的handler是同步消息 

public Handler() {
        this(null, false);
    }

   /**
     * @hide 
     */
    public Handler(Callback callback, boolean async) {
            //...不让我们用async
         mAsynchronous = async;
    }
    
// 该方法被hide了，不让我们异步
//我们构造的handler async = false
//那这个异步到底谁在用？
 
    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (mAsynchronous) {
            //最终作用到message上
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

	Message 的setAsynchronous注释：
    // Sets whether the message is asynchronous, meaning that it is not
    // subject to {@link Looper} synchronization barriers.
synchronization barriers是什么 直译过来是 “同步障碍/栅栏”
即如果是异步的消息，这个消息是有  “栅栏” 的
//Asynchronous messages are exempt from synchronization barriers
//翻译：异步消息不受同步障碍的影响    
// The synchronization barrier ensures that the invalidation
// request is completely handled before resuming
保证绘制请求在resume之前被完全处理


scheduleTraversals（）方法会执行mHandler.getLooper().getQueue().postSyncBarrier()
        
本质是控制message的优先级
```

1.在子线程更新UI

```kotlin
//CalledFromWrongThreadException 只有创建view的线程可以操作view
onResume时
即ActivityThread.handleResumeActivity时
创建ViewRootImp
调用requestLayout时，checkThread()会抛出该错误
```

Handler构造需要Looper（子线程创建Handler异常）
Looper.prepare()将Looper绑定到当前线程（ThreadLocal）
Looper持有MessageQueue,将消息不断的取出，发送到Message的target






