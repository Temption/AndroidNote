## Volley

### 1.优点：

- 请求队列和请求优先级
- 请求Cache和内存管理
- 扩展性性强
- 可以取消请求

### 2.使用

##### 	2.1 创建一个请求队列`RequestQueue，单例，懒汉式

​			方法：添加，取消请求

##### 	2.2创建请求

​			Request接口

​			JsonObjectRequest+StringRequest

​			 getParams，getHeaders方法

​			自定义请求：封装个dialog，或者加上Gson 解析成想要的数据类型

##### 	2.3图片处理

​		NetworkImageView + ImageLoader  （自己实现ImageCache）

​		setUrl方法，先会判断当前ImageView的URL和新传入的URL是否一致，如果相同，就不用再发送http请													                			

​		求了，如果不同，那么就使用ImageLoader对象来发送http请求获取图片

##### 	2.4缓存

##### 	2.5请求优先级  

​		Normal, Low, Immediate, High



请求的处理是由CacheDispatcher和NetworkDispatcher，thread的子类，来完成的，它们的run方法通过一个死循环不断去从各自的队列中取出请求，进行处理，并将结果交由ResponseDelivery，由其实现类ExecutorDelivery将回掉结果post给UI线程








​		

