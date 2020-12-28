

[TOC]



## GreenDao

##### 1.简介

-   ORM:object relation  mapping ，即对象关系映射对象模型与关系模型的一种映射。数据库-->对象,一个bean对应一张表

- DAO是一种ORM框架 Data Access Object 是一个数据访问接口夹在业务逻辑与数据库资源中间

    ​

##### 2.优点

- 让业务代码访问对象，而不是数据库表
- 隐藏了面向对象的逻辑sql查询详情  -不用写sql
- 无须处理数据库实现

##### 3.使用

- new DaoMaster.DevopenHelper-->getDataBase -->DaoMaster-->Daossesion-->Dao->增删查改
- 如果需要升级，需要重写DevOpenHelper,实现onUpgrade逻辑：**判断版本号，创建临时表，迁移数据，删除旧表，重命名临时表**

##### 4.注解

@Entity 用于标识这是一个需要Greendao帮我们生成代码的bean

@Id 标明主键，括号里可以指定是否自增

@Property 用于设置属性在数据库中的列名（默认不写就是保持一致）

@NotNull 非空

@Transient 标识这个字段是自定义的不会创建到数据库表里

@Unique 添加唯一约束