[TOC]

## Java、Groovy、Gradle的关系含义及Gradle插件开发

### 1.Groovy简介

​	运行在JVM上,既有面向对象的特性又有纯粹的脚本语言的特性,http://www.groovy-lang.org/syntax.html

### 2.Gradle简介

- Groovy 语言
-  Gradle是一个使用[Groovy](https://link.jianshu.com/?t=http://www.groovy-lang.org/index.html)语言编写的框架，官方提供了Java-Plugin、Maven-Plugin等插件
- 构建项目真正起作用的是基于gradle框架的Gradle Plugin（非官方）

```groovy
//引入了构建android应用的插件
plugin: "com.android.application"

//位置
buildscript {
    repositories {
        jcenter()//依赖库位置
    }
    dependencies {
     	//gradle插件
        classpath 'com.android.tools.build:gradle:2.1.2'
    }
}
```





