LiveData
使用：

1. ViewModel类

```java

public LiveData<String> getMediatorLiveData(String url) {
//创建 中介     LiveData
MediatorLiveData<String> mediatorLiveData = new MediatorLiveData<>();
	//远程liveData
	 LiveData mutableLiveData = mutableLiveData().getRemoteLiveData();
		//关联中介与远程
	 mediatorLiveData.addSource(mutableLiveData, new Observer<BaseResp<String>>() {
            @Override
            public void onChanged(BaseResp<ReportRoomDto> resp) {
					//通知到容器
               mediatorLiveData.setValue(new InfoResult(true));
            }
        });	  	
	return mediatorLiveData;
}
```



2.容器

```java

//创建ViewModel，持有当前容器的生命周期
LiveDataViewModel mViewModel = ViewModelProviders.of(this).get(LiveDataViewModel.class);
//观察livedata
mViewModel.getMediatorLiveData("").observe(LifeCycleOwener owener,new Observer<Result>(){
    @Override
    public void onChanged(Result result){
        //ui更新
    }   
});
//3.订阅时机
```

activity --onCreate() --subcribeUI(LiveData)

fragment --onAcCreated -- subscribeToModel(Model)

3.
// `observe`通常，只有active的owener会收到变化通知

 LifecycleOwner is considered as active, if its state is `STARTED`     从onStart 到 onPause的 change都能观察到



// `observeForever`无论owener什么状态都会获得通知，直到remove

An observer added via `observeForever(Observer)` is considered as always active and thus will be always notified about modifications. For those observers, you should manually call `removeObserver(Observer)`.

4.mutableLiveData 易变的LiveData，提供两个方法改变value

- //main/ui   livedata.setValue();
- //io/cpu    livedata.postvalue();

5.MediatorLiveData 用于对多个LiveData进行合并

6.

````java
 public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        assertMainThread("observe");
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            return;
        }
		//同一个 observer 不能 bind不同的 lifecyle 
        LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
        ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
        if (existing != null && !existing.isAttachedTo(owner)) {
            throw new IllegalArgumentException("Cannot add the same observer"
                    + " with different lifecycles");
        }
        if (existing != null) {
            return;
        }
        owner.getLifecycle().addObserver(wrapper);
    }
````

7.SingleLiveEvent,（onkyFromUser）只有setValue才会调用该方法，生命周期引起的变化不会激活该方法

```java
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 * <p>
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 * <p>
 * Note that only one observer is going to be notified of changes.
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private static final String TAG = "SingleLiveEvent";

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @Override
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {

        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");
        }

        // Observe the internal MutableLiveData
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(t);
                }
            }
        });
    }

    @Override
    @MainThread
    public void setValue(@Nullable T t) {
        mPending.set(true);
        super.setValue(t);
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
}

```

