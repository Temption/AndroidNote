# Scrollview嵌套RecyclerView攻略



## 解决问题：1.滑动冲突  2.显示不完整

### 1.滑动冲突：

​	1.1.重写ScrollView

```java
package com.cexi.yunzehui.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
/**
 * Created by Hsh on 2017/4/6.
 * 解决内嵌
 *
 * 横向滑动或者竖向的view 滑动不灵敏的问题
 * 外层拦截法
 *核心：不拦截横向滑动,拦截竖向
 */

public class NestedScrollView extends ScrollView {

    private float mDownPosY;
    private float mDownPosX;

    public NestedScrollView(Context context) {
        super(context);
    }
    public NestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosX = x;
                mDownPosY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = Math.abs(x - mDownPosX);
                final float deltaY = Math.abs(y - mDownPosY);
                if (deltaX > deltaY) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}

```

1.2.在设置适配器之前加上西面这句

```java
//滑动惯性丧失的问题，我们取消RecyclerView的滚动，让他跟着scrollview滚
rvVertical.setNestedScrollingEnabled(false);
```



### 2.显示问题

主要就是条目缺失问题

当第一个window不出现RecyclerView时，我们的显示是没问题的，反之，只能显示一条，或两条这个问题是同事发现的，我们当时都觉得很诡异另外，这是一个深坑，可能这个手机显示没问题，你换个高一点的手机，RecyclerView正好在第一个window露了头，恩恩，结果就是，就剩那么一两条数据了这时，我们怎么处理呢？

有两种方案

2.1写死条目高度，

> 网络请求获得条目数量，根据条目数量，动态设置RecyclerView高度缺点：这时RecyclerView的高度是固定的，RecyclerView需要一次就把所有条目加载完，会造成recyclerview无法复用，此时的recyclerview基本上就与LinearLayout无异，当条目数量很少时你可以这样做，当条目数量很多时，就不要考虑了，丧失了复用，对性能是很大的伤害。

2.2.这是我推荐的方法

> 在外层嵌套一个相对布局即可

```xml
 			<RelativeLayout
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 android:descendantFocusability="blocksDescendants">
            <android.support.v7.widget.RecyclerView
                android:id="@+id/rv_vertical"
                android:layout_width="match_parent"
                android:layout_height="match_parent">
            </android.support.v7.widget.RecyclerView>
            </RelativeLayout>
```





