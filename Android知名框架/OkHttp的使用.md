## OkHttp源码解读

### 1.基本使用

```java
client.newCall(request).execute(); 
client.newCall(request).enqueue(Callback responseCallback);         
```

### 2.源码分析

#### 2.1实际Call的调用

```java
final class RealCall implements Call {
		//同步执行
    public Response execute() throws IOException {
           client.dispatcher().executed(this);	
           Response result = getResponseWithInterceptorChain();
    }	

    //异步：enqueue，call加入**runningAsyncCalls**,并交给线程池执行getResponseWithInterceptorChain
    synchronized void enqueue(Callback responseCallback) {
        //AsyncCall是个runnable
        client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }


    final class AsyncCall extends NamedRunnable {
        private volatile AtomicInteger callsPerHost = new AtomicInteger(0);
        void executeOn(ExecutorService executorService) {
          executorService.execute(this);
        }
        //实际的run
        protected void execute() {
            //响应与回调
            Response response = getResponseWithInterceptorChain();
            responseCallback.onResponse(RealCall.this, response);
        }
    }
	Response getResponseWithInterceptorChain() throws IOException {
		//构造拦截器
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
		//缓存
    	interceptors.add(new CacheInterceptor(client.internalCache()));
        interceptors.add(new CallServerInterceptor(forWebSocket));
		//构造责任链
     	Interceptor.Chain chain =  new RealInterceptorChain(interceptors);
		//从初始位置逐个取出拦截器进行拦截，拦截器拦截过程中可以终止
		chain.proceed()
	}
}	

public final class RealInterceptorChain implements Interceptor.Chain {
  public Response proceed(Request request, Transmitter transmitter, @Nullable Exchange exchange){
       // Call the next interceptor in the chain.
    RealInterceptorChain next = new RealInterceptorChain(interceptors, transmitter, exchange,
        index + 1, request, call, connectTimeout, readTimeout, writeTimeout);
    Interceptor interceptor = interceptors.get(index);
	//怼事件进行消费，将拦截链交给下一个拦截器
    Response response = interceptor.intercept(next);
	return response;
  }
}



```

#### 2.2Dispatcher控制call执行分发

   ```java

public final class Dispatcher {
  void enqueue(AsyncCall call) {
    synchronized (this) {
      readyAsyncCalls.add(call);
      // Mutate the AsyncCall so that it shares the AtomicInteger of an existing running call to
      // the same host.
    promoteAndExecute();
  }

  private boolean promoteAndExecute() {
    assert (!Thread.holdsLock(this));

    List<AsyncCall> executableCalls = new ArrayList<>();
    boolean isRunning;
    synchronized (this) {
	//	遍历readyAsyncCalls，判断是否满足下列条件，如果满足则加入runningAsyncCalls
      for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
        AsyncCall asyncCall = i.next();
        if (runningAsyncCalls.size() >= maxRequests) break; // Max capacity.def = 64
        if (asyncCall.callsPerHost().get() >= maxRequestsPerHost) continue; // Host max capacity. def=5
        i.remove();
        asyncCall.callsPerHost().incrementAndGet();
        executableCalls.add(asyncCall);
        runningAsyncCalls.add(asyncCall);
      }
      isRunning = runningCallsCount() > 0;
    }
	// 放入线程池执行
    for (int i = 0, size = executableCalls.size(); i < size; i++) {
      AsyncCall asyncCall = executableCalls.get(i);
      asyncCall.executeOn(executorService());
    }
    return isRunning;
  }

//执行AsyncCall的线程池
  public synchronized ExecutorService executorService() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
          new SynchronousQueue<>(), Util.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
  }

}
   ```



4.连接池


____

### OkHttp缓存策略

> 服务器支持缓存，即响应的字段中包含Header:Cache-Control, max-age=xxx,(本地存储时间)

```java
OkHttpClient okHttpClient = new OkHttpClient();
OkHttpClient newClient = okHttpClient.newBuilder()
               .cache(new Cache(mContext.getCacheDir(), 10240*1024))
               .connectTimeout(20, TimeUnit.SECONDS)
               .readTimeout(20, TimeUnit.SECONDS)
               .build();
```



> 服务器不支持缓

```java
//需要使用Interceptor来重写Respose的头部信息，从而让okhttp支持缓存。
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Response response1 = response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                //cache for 30 days
                .header("Cache-Control", "max-age=" + 3600 * 24 * 30)
                .build();
        return response1;
    }
```















