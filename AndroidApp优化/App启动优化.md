## App启动优化

1.启动过程细分：

- Application的onCreate，第一个Activity（WelcomeA）的onCreate,onStart,onResume，如果在这几个方法中进行了跳转，跳转后的Activity也需要进行优化

2.黑白屏解决（体验优化）

-  继承AppTheme,重新指定windowbackground-drawable-layer-list,设置给启动Activity

3.启动时间优化

- 启动时间查询

  adb shell am start -W packageName/MainActivity

  ThisTime:最后一个Activity启动时间
  Total:所有Activity启动时间
  WaitTime:总启动时间

```java
void onCreate(){
    File file = Environment.getExternalStorageDirectory(),"app.trace");
 	 Debug.startMethodTracing(file.getAbsolutePath);
//目的段
	Debug.stopMethodTracing();
//adb pull /storage/emulated/0/app.trace
}
```

4.UI渲染流程优化

- XML优化 层级优化，
- 过度绘制

5.内存

- GC负责堆内存的管理
- 内存泄漏分析  shallowSize
- 导出*.hprof 后， 使用  hprof -conv -z  xxx.hprof     mat-xxx.hprof
- MAT工具

6.handler泄漏要根据业务来区分，泄漏几秒 也不会出大问题

- 软引用， 不影响外部类的回收，缺点  不确定什么时间会被回收，可能会影响业务
- static   对外部类不再引用，但会导致类加载消耗过多

7.反射置空

8.webview 内存泄漏 单开进程

9.大图片加载：BitmapRegionDecoder

10.无损压缩（哈夫曼算法） 