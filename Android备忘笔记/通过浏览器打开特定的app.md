### 通过超链接打开特定的app

#### 1.Html

```html
<a href="[scheme]://[host]/[path]?[query]">启动应用程序</a> 
<a href="scheme://host.app/pathPrefix?name=zhangsan&age=26">启动应用程序</a>  
```

- scheme：判别启动的App
- host：适当记述
- path：传值时必须的key          ※没有也可以
- query：获取值的Key和Value  ※没有也可以

#### 2.app清单文件

```xml

intent-filter的内容【android.intent.action.MAIN】和 【android.intent.category.LAUNCHER】这2个
如果同属于一个activity，则必须并列
<intent-filter>  
    <action android:name="android.intent.action.MAIN"/>  
    <category android:name="android.intent.category.LAUNCHER" />  
</intent-filter>  
<intent-filter>  
    <action android:name="android.intent.action.VIEW"/>  
    <category android:name="android.intent.category.DEFAULT" />  
    <category android:name="android.intent.category.BROWSABLE" />  
      
    <data android:scheme="scheme" android:host="host.app" android:pathPrefix="/pathPrefix"/>  
</intent-filter> 
```

#### 3.代码中

```java
onCreate()中,可以获得传值：

Intent intent = getIntent();  
String action = intent.getAction();  
  
if(Intent.ACTION_VIEW.equals(action)){  
    Uri uri = intent.getData();  
    if(uri != null){  
        String name = uri.getQueryParameter("name");  
        String age= uri.getQueryParameter("age");  
    }  
}
```

