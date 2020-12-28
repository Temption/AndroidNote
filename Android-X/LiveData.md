LiveData
使用：

1 ViewModel类

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

