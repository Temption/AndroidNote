[TOC]



## Fragment管理框架：Navigation

#### 1.创建

##### 1.1静态创建NavHostFragment

- 资源res → folder:navigation  → navigation resource.xml

```xml
        <?xml version="1.0" encoding="utf-8"?>
        <navigation xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            xmlns:tools="http://schemas.android.com/tools"
            app:startDestination="@id/page1Fragment">

     <!--startDestination: 配置第一个fragment-->
     <!--action: 规定跳转行为，有唯一id，一个fragment可以有多个action，根据不同action进行跳转-->
			
            <fragment
                android:id="@+id/page1Fragmen
                android:name="com.qingmei2.samplejetpack.ui.main.MainPage1Fragment"
                tools:layout="@layout/fragment_main_page1">
                <action
                    android:id="@+id/action_1"
                    app:destination="@id/page0Fragment" />
               <action
                    android:id="@+id/action_2"
                    app:popUpTo="@id/page2Fragment" />
            </fragment>
        </navigation>

//根据action Id跳转
Navigation.findNavController(Activity).navigate(actionId);
```

##### 1.2动态创建

```java
NavHostFragment finalHost = NavHostFragment.create(R.navigation.example_graph);
getSupportFragmentManager().beginTransaction()
    .replace(R.id.nav_host, finalHost)
     // this is the equivalent to app:defaultNavHost="true"
    .setPrimaryNavigationFragment(finalHost) 
    .commit();
```

#### 2.使用NavigationFragment

```xml
   <fragment android:id="@+id/container"
             //固定
            android:name="androidx.navigation.fragment.NavHostFragment"
           
             //指向xml静态配置
            app:navGraph="@navigation/login_navigation"
            //接受回退事件
            app:defaultNavHost="true"/>
```

#### 2.跳转

- 根据xml配置的acitionId进行跳转

  ```java
   Navigation.findNavController(view).navigate(R.id.viewTransactionsAction);
  ```

- activity回退事件处理

  ```java
  @Override
  public boolean onSupportNavigateUp() {
      return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
  }
  ```

#### 3.传参

- 

```java
//1.
navHostFragment
         .getNavController()
         .getGraph()
         .addArgument("argKey",new NavArgument.Builder().setDefaultValue("argValue").build())


//2.
 FromFragmentDirection.Action action = FromFragmentDirections.actionid()
NavController.navigate()
DestFragmentArgs.fromBundle(getArguments())
```

