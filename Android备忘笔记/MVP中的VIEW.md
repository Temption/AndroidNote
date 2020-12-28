# MVP

###   Purpose: Separating interface from logic    	   	    解耦 M与V

![![img](file:///F:/%E7%BB%8F%E9%AA%8C%E6%80%BB%E7%BB%93typora/image/MVC-MVP.png?lastModify=1520221807)![img](file:///F:/%E7%BB%8F%E9%AA%8C%E6%80%BB%E7%BB%93typora/image/MVC-MVP.png?lastModify=1505354192)MVC-MVP](F:\经验总结typora\image\MVC-MVP.png)

### 

## PRESENTER

- the middle man between view and model
- 从Model拿到数据给View，从View得到用户操作修改Model
- 不允许View访问Model,这是MVP与MVC的不同之处

## View

- Calling a method from the presenter 
- 接收用户行为并反馈Activity，Fragment,Widget

## Model

- 数据存储与读取功能功能

- 业务数据逻辑层

  ​

### 交互规则：Model与View之间的交互由Presenter代理完成

### todo-app:

- Contract:抽象P与V
- V的实现：Actcivity或者Fragment
- P的实现：拿到M与V作交互