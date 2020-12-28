# 屏幕适配

## 尺寸适配

dpi  (dots per inch)：单位尺寸（英寸）像素的多少

dp的定义：Density-independent Pixels  ，官方解释义：为当dpi为160时，1dp就是1px

```java
public static int px2dip(Context context, float pxValue) {         
       final float density = context.getResources().getDisplayMetrics().density;         	      		return (int) (pxValue / density + 0.5f);    
  }
  px = dp * density
//公式: density = dpi/160  密度单位，160dpi时,denstity = 1 
```



|     dpi简称      |      具体区间       |   1dp代表的px值    |
| :------------: | :-------------: | :------------: |
|      ldpi      | [120dpi，160dpi) | [0.75px ,1 px) |
|      mdpi      | [160dpi，240dpi) |  [1px,1.5px)   |
| layout default |                 |                |
|      hdpi      | [240dpi，320dpi) |  [1.5px,2px)   |
|     xhdpi      | [320dpi，480dpi) |   [2px,3px)    |
|     xxhdpi     | [480dpi，640dpi) |   [3px,4px]    |

### 1.自定义布局适配

1. 获取屏幕宽高，与ui图基准分别相比算出缩放比例
2. 自定义layout，重写onMeasure方法
3. 子view使用ui图px即可

### 2.百分比布局适配

原理：重写ViewGroup的onMeasure(),以父容器实际宽高作为参考，* 百分比 得出 子view宽高

### 3.动态修改Density（推荐）

使得所有手机宽度为固定dp值
dp*density = px

```java
package com.source.sourcecode;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 *1.放在BaseAcitivity的setContentView之前
 *2.放在Application 对Activity注册的LifeCycle 回调方法 onActivityCreated中
 *3.width修改为ui图参考的宽度，布局里面直接写dp
 */

public class DensityUtil {
    private static final float WIDTH = 360;
    private static  float appDensity;
    private static  float appScaleDenstity;
    private static  void setDensity(final Application application, Activity activity){
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (appDensity == 0) {
            appDensity = displayMetrics.density;
            appScaleDenstity = displayMetrics.scaledDensity;
            //添加字体大小变化监听的回调
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0){
                        appScaleDenstity = 	application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }
                @Override
                public void onLowMemory() {

                }
            });
        }
        float targetDensity = displayMetrics.widthPixels / WIDTH;
        float targetScaleDensity = targetDensity * (appScaleDenstity / appDensity);
        int targetDensityDpi = (int) (targetDensity * 160);
        //进行替换
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        dm.density = targetDensity;
        dm.scaledDensity =targetScaleDensity;
        dm.densityDpi = targetDensityDpi;
    }
}
```

## 9.0刘海屏适配

- 如果非全屏模式，则不受刘海屏影响，刘海屏只会影响到状态栏
- 全屏模式，如果未适配，系统会将屏幕移动

