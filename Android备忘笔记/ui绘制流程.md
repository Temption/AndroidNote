## ui绘制流程

#### ![view绘制](.\image\view绘制流程\view绘制.png)

->setContentView 

->XML解析

->ActivityThread resume

->WindowManager添加

->ViewRootImpl绘制



```java
class ActivityThread{ 
  handleResumeActivity(){
    WindowManagerImpl.addView(decor,layoutparams);
		//WindowManagerGlobal.addView(decorView, params, mDisplay, )
  }
}
```

实现是
```java
class WindowManagerGlobal{
   void addView(){
       ViewRootImpl root = new ViewRootImpl(context,display) ;
       root.setView(decorview, params, panelParentView);
    }
}
```



```java
class ViewRootImpl{
	setView()--requestLayout()--checkThread()+scheduleTraversals()
      void scheduleTraversals(){
   			//mChoreographer维护一个Handler，负责frame

      mChoreographer.postCallback(
                          Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
        final class TraversalRunnable implements Runnable {
              @Override
              public void run() {
                  doTraversal();
              }
          }
      }
      //doTraversal()-->performTraversals()-->performMeasure()--performLayout()--performDraw()
		
}
```



### Measure

MeasureSpec：32位int   SpecMode (2) + SpecSize(30)

DecorView MeasureSpec由Window大小与自身LayoutParams决定

ViewGroup onMeasure --

View 的 MesureSpec 由父容器MeasureSpec 与 自身的LayoutParams共同决定	

VIew   onLayout  - setFrame


ViewGroup onLayout - layoutChild







