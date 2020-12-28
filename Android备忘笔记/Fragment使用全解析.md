## Fragment使用全解析

### 1.布局中作为view使用

### 2.动态添加、更新、以及删除Fragment

- FrameLayout  占位

  ```java
    getFragmentManager()
   					.beginTansaction()
  					.replace(R.id.id_framlayout,new ContentFragment());
  transaction负责fragment的add  hide show  replace
  ```

### 3.常用Api

```java
-add
-remove
-replace=add+remove other 如果不添加事务到回退栈，前一个Fragment实例会被销毁，自身出栈也会被销毁
-show/hide 不销毁Fragment (保存用户操作),tab控制 fragment显示或者隐藏
-detach与attach 销毁重建Fragment中的View
-commit必须在 Activity.onSaveInstance之前调用，并且一个事务只能提交一次
-addToBackStack()  点返回键可以回退到之前的fragment，通常与show/hide一起使用，与replace一起回出问题
```

### 4.与Activity进行通信

#### 4.1.A-->F

- getFragmentManager.findFragmentByTag()或者findFragmentById()获得任何Fragment实例

#### 4.2.F-->A

​	接口通知，onAttach onDetach获得actvity对象，用于注册/解除

### 5.重建影响

```java
长时间位于后台（接听电话，按home键后长时间未resume），旋转屏幕，如果ConfigureChanged没有配置该原因，会使Activity重建，acticity会保存一些具有id的view的state,recyclerview不会恢复数据
1.Fragment会跟着Activity重新创建，因此就会出现两个相同的Fragment,
	fragment是一个相对独立的大View,findTagById
2.通过检查onCreate的参数Bundle savedInstanceState就可以判断，当前是否发生Activity的重新创建
3.为了防止Fragment重建，在savedInstanceState==null时，才创建Fragment实例，这样就保证一个Fragment
4.数据恢复
		Activity- 在Activity的onSaveInstanceState保存数据，在onCreate中恢复数据	
5.无布局Fragment可以用来作为数据的壳,setRetainInstance(true)，此时当Activity重建时，该Fragment不会销毁
```

#### Demo

##### Demo1:without backStack 解决重叠 

```java
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private Fragment fragmentA;
    private Fragment fragmentB;
    private Fragment fragmentC;
   	private int position = 0;
   @Override
   protected void onSaveInstanceState(Bundle outState) {
    	outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     	//防止由于activity重建导致创建两个fragment
         if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
      	initFragment();       
    }
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }
    private void initFragment() {
        fragmentManager=getSupportFragmentManager();
        fragmentA= (FragmentA) fragmentManager.findFragmentByTag("A");
        fragmentB= (FragmentB) fragmentManager.findFragmentByTag("B");
        fragmentC= (FragmentC) fragmentManager.findFragmentByTag("C");
        selectFragment(position);
    }

    //添加或恢复对某个Fragment的引用
    private void selectFragment(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (fragmentA == null) {
                    fragmentA = new FragmentA();
                  //尽量使用FragmentA.newInstance();静态方法传递参数
                    transaction.add(R.id.fl_content, fragmentA, "A");
                } else {
                    transaction.show(fragmentA);
                }
                break;
            case 1:
                if (fragmentB == null) {
                    fragmentB = new FragmentB();
                    transaction.add(R.id.fl_content, fragmentB, "B");
                } else {
                    transaction.show(fragmentB);

                }

            case 2:
                if (fragmentC == null) {
                    fragmentC = new FragmentC();
                    transaction.add(R.id.fl_content, fragmentC, "C");
                } else {
                    transaction.show(fragmentC);
                }
                break;
        }
        transaction.commit();
    }
    private void hideFragments(FragmentTransaction transaction) {
        if (fragmentB != null) {
            transaction.hide(fragmentB);
        }
        if (fragmentA != null) {
            transaction.hide(fragmentA);
        }
        if (fragmentC != null) {
            transaction.hide(fragmentC);
        }
    }
 	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		outState.putInt("position",position);
    }

```

##### Demo2 ：with BackStack

````java
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
		 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
		//也可以findByTag 
        if (savedInstanceState == null) {
            radioSearchRecommendFragment = RadioSearchRecommendFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_container, radioSearchRecommendFragment,   RadioSearchRecommendFragment.class.getName())
                    .commit();
        }
    }
	//切换并添加到回退栈
	private void switch(){
      Fragment radioSearchFragment = fragmentManager.findFragmentByTag("tag");
        if (radioSearchFragment == null) {
            radioSearchResultFragment = RadioSearchResultFragment.newInstance(content, "tag");
            fragmentManager.beginTransaction()
                    .hide(radioSearchRecommendFragment)
                    .add(R.id.fl_container, radioSearchResultFragment,"tag")
                    .addToBackStack(null)
                    .commit();
        } else {
            radioSearchResultFragment.searchKeyWord(content,false);
        }
	}
}
````

