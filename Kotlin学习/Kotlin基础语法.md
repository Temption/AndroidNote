## Kotlin基础语法

[TOC]



#### 1.var (variable)可以被重新赋值，val(value) 不可以

```kotlin
//value 不可修改，但可以自定义getter
val size: Int
    get() { // 👈 每次获取 size 值时都会执行 items.size
        return items.size
    }
```



#### 2.关于null

```kotlin
  var nullable_str: String? = "May be declare nullable string"
//1. ?.  可能返回null
 nullable_str?.length
//2. ?.let{ } 代替 if not null
    nullable_str?.let { print(nullable_str.length) } 
//length可能为空
    var length :Int? = nullable_str?.length
// "Elvis operator", ?:  空右，不空左边
    val heightInSquare = length ?: 0
// !! double-bang ，"!" is often called a "bang"
    var len = nullable_str!!.length   //may throw NullPointerException 

```

#### 3. 集合

```kotlin
//不可变集合
    val school = listOf("mackerel", "trout", "halibut")
//可变
    val listWithNulls = mutableListOf("tuna", "salmon", null)
  school?.let { println(it) } // 如果不是null 就输出
//过滤掉集合中的null
    val nonNullList: List<String> = listWithNulls.filterNotNull()
```

#### 4.数组

```kotlin
val mix = arrayOf("fish", 2)
val numbers = intArrayOf(1,2,3)

两个数组 可以直接 "+"，构造“和数组”，不同类型数组没有相加意义
```

#### 5.loop

while是先判断后循环 ，而do–while是先循环后判断。

#### 6.$引用

```kotlin
val temperature = 10
val message = "The water temperature is $temperature,and it's ${ if (temperature > 50) "too warm" else "OK" }."
//The water temperature is 10,and it's OK.
```

#### 7.原生字符串

```kotlin
val name = "world"
val myName = "kotlin"
           👇
val text = """
      Hi $name!
    My name is $myName.\n
"""
println(text)
```



#### 7.参数

```kotlin
//可选参数的值可以是函数
fun sayHi( age: Int,name: String = "world",whom: String = "anyOne") {
    ...
}
//一般，有默认值的参数放到后边


```

#### 8.filter ,map

```kotlin

val intArray = intArrayOf(1, 2, 3) 
//🏝️fliter
// [1, 2, 3]
      ⬇️
//  {2, 3}

//            👇 注意，这里变成了 List
val newList: List = intArray.filter { i ->
    i != 1 // 👈 过滤掉数组中等于 1 的元素
}

//🏝️map
//  [1, 2, 3]
       ⬇️ 
//  {2, 3, 4}
val newList: List = intArray.map { i ->
    i + 1 // 👈 每个元素加 1
}

//🏝️flatmap
//          [1, 2, 3]
               ⬇️
// {"2", "a" , "3", "a", "4", "a"}
intArray.flatMap { i ->
    listOf("${i + 1}", "a") // 👈 生成新集合
}

```

#### 10.lambda是对象，匿名函数不是函数

- 在 Kotlin 里，有一类 Java 中不存在的类型，叫做「函数类型」，这一类类型的对象在可以当函数来用的同时，还能作为函数的参数、函数的返回值以及赋值给变量；
- 创建一个函数类型的对象有三种方式：双冒号加函数名、匿名函数和 Lambda；
- 一定要记住：双冒号加函数名、匿名函数和 Lambda 本质上都是函数类型的对象。在 Kotlin 里，匿名函数不是函数，Kotlin 的 Lambda 可以归类，它属于函数类型的对象。

```kotlin
val waterFilter = { dirty: Int -> dirty / 2 }
// lambda 作为变量
println(waterFilter(20)) //等同于println(waterfilter.invoke(20))

fun updateDirty(dirty: Int, operation: (Int) -> Int): Int {
   return operation(dirty)
}
//lambda 作 参数--高阶函数
println(updateDirty(30, waterFilter))

// named function 作 参数
fun increaseDirty( start: Int ) = start + 1
println(updateDirty(15, ::increaseDirty))

//lambda可以放在括号外
var dirtyLevel = 19;
dirtyLevel = updateDirty(dirtyLevel) { dirtyLevel -> dirtyLevel + 23}
println(dirtyLevel)

```

- [Last parameter call syntax](https://kotlinlang.org/docs/reference/lambdas.html#passing-a-lambda-to-the-last-parameter)

#### 11.constructor and init 

```kotlin
class Student(private var name: String, private var age: Int) {

    private var gender: Int? = null

    init {
        name = "$name inited"
    }

    constructor(name: String, age: Int, gender: Int) : this(name, age) {
        this.gender = gender
    }

    fun show() {
        print("该学生的姓名是：${name},年龄是:${age},性别是：${gender}")
    }

    fun judge() {
        if (age > 30 && gender == 1) {
            print("bad")
        } else {
            print("good")
        }
    }
}
```

#### 12.subclasses 

```kotlin
//open 的类可以被继承
//interface 与 abstract 的类 默认是open的
```

#### 13.interface delegation

```kotlin
interface IA{
    fun a()
}

interface IB{
    val b: String
}

class A (val str: String) : IA{
    override fun a() {
        print(str)
    }
}

//单例,用object代替class
object B : IB{
    override val b = "b_str"
}
class P: IA,IB {
    override fun a() {
    }
    override val b: String = "";
}

//interface delegation ：Q将IB委派给B实现
class Q:IA, IB by B{
    override fun a() {
    }
}

//构造中指定
class R (b :IB = B):IA, IB by b{
    override fun a() {
    }
}

//所有实现都委派出去，没有{}
class R (b :IB = B):IA by A("str"), IB by b
```

#### 14. == 

```
"=="代表equals() (structural equality) 比较属性
"===" referential equality 比较引用地址
 To copy the contents to a new object, use the copy() method

//The copy(), equals(), only reference properties defined in the primary constructor
```

#### 15.destructing

```kotlin
val rock = decoration.rock
val wood = decoration.wood
val diver = decoration.diver
//等价于  Destructuring Declarations
val (rock, wood, diver) = decoration
//skip some by using _
val (rock, _, diver) = decoration
```

#### 16.enum

```kotlin
enum class Direction(val degrees: Int) {
    NORTH(0), SOUTH(180), EAST(90), WEST(270)
}
```

#### 17.sealed class

```kotlin
//只能被同一个文件中的类使用
sealed class Seal
class SeaLion : Seal()
class Walrus : Seal()

fun matchSeal(seal: Seal): String {
   return when(seal) {
       is Walrus -> "walrus"
       is SeaLion -> "sea lion"
   }
}
```

#### 18.pair

```kotlin
//Create a pair
val equipment = "fish net" to "catching fish"
println("${equipment.first} used for ${equipment.second}")

//Create a triple
//Triple() with 3 values. Use .first, .second and .third to refer to each value
val numbers = Triple(6, 9, 42)
println(numbers.toString())
println(numbers.toList())
//⇒ (6, 9, 42)
//[6, 9, 42]

val equipment2 = ("fish net" to "catching fish") to "equipment"
println("${equipment2.first} is ${equipment2.second}\n")
println("${equipment2.first.second}")
//⇒ (fish net, catching fish) is equipment
//⇒ catching fish

//Destructure a pair
val (tool, use) = equipment
println("$tool is used for $use")
//⇒ fish net is used for catching fish
```

#### 18.数组

```kotlin
val array7 = Array(4, { i -> i * i })  //0,1,4,9
// 遍历数组元素
for (item in array7) {
    println(item)
}
// 遍历数组下标
for (item in array7.indices) {
    println(item)
}
// 迭代器遍历数组1
val it = array7.iterator()
for (item in it.iterator()) {
    println(item)
}
// 迭代器遍历数组2
val it1 = array7.iterator()
it1.forEach {
    println(it)
}
// forEach遍历数组
array7.forEach {
    println(it)
}
```

#### 19.



#### 19.Collections

```kotlin
val list = listOf(1, 5, 3, 4)
println(list.sum())

//需要知道如何计算
val list2 = listOf("a", "bbb", "cc")
println(list2.sumBy { it.length })

//listOf() 创建不可变的 List，mutableListOf() 创建可变的 List。
val mutableList = mutableListOf<Int?>(1,5)
mutableList.add(2)
mutableList.add(2)
mutableList.add(null)
mutableList.filterNotNull()
mutableList.forEach{
    println(it)
}
for(item in mutableList){
    println(item)
}
```

### 20.区间 Range

```
//[0,100]  :IntRange
0..100   0 rangeTO(100)  
//[100,0]  :IntRange
100.downTo(0)
1 until 100 表示[0,100)
i in 0..100 判断i是否在区间[0,100]中
```

#### 20.for 遍历，循环

```kotlin
//rangeTo 相当于 .. 运算符
var mutableList: MutableList<Int> = mutableListOf(1, 2, 3, 4)
mutableList.forEach {
    println("Mutable List Elements:$it")
}
for (value in mutableList) {
    print("value:$value")
}
for ((index,e) in mutableList.withIndex()){
    println("下标=$index----元素=$e")
}
val cures = hashMapOf("white spots" to "Ich", "red sores" to "hole disease")
println(cures.get("white spots"))
println(cures["white spots"])
//⇒ Ich Ich
println(cures["nulla"])
//⇒ null
println(cures.getOrDefault("nulla", "sorry, I don't know")))
//⇒ sorry, I don't know

//If you need to do more than just return a value, Kotlin provides the getOrElse() function.
cures.getOrElse("bloating") {"do sth"}

// Mutable just means able to change, lets you put and remove items,
//immutable means unable to change. 
```

#### 21.const,伴生对象

```kotlin
//类似于 public static final
//but,Kotlin does not have a concept of class level constants.
//如果想要在类中使用const,需要使用companion object
class MyClass {
    companion object {
        const val CONSTANT3 = "constant in companion"
    }
}

//companion object 与 regular objects 的区别：
//Companion object 随类加载，本质是静态单例内部类
//普通 object 随对象加载
//为了在java中调用，需要给 Companion object的静态成员添加@JvmStatic注解
```

##### 22.标准函数

```kotlin
// let //表示object不为null的条件下，才会去执行let函数体
object?.let{
}

// with 适用于调用同一个类的多个方法时，可以省去类名重复，直接调用类的方法即可
override fun onBindViewHolder(holder: ViewHolder, position: Int){
   val item = getItem(position)?: return
   with(item){
      	holder.tvNewsTitle.text = StringUtils.trimToEmpty(titleEn)
	   holder.tvNewsSummary.text = StringUtils.trimToEmpty(summary)
	   holder.tvExtraInf.text = "难度：$gradeInfo | 单词数：$length | 读后感: $numReviews"
   }
}
//run = with+let
override fun onBindViewHolder(holder: ViewHolder, position: Int){
  getItem(position)?.run{
      	holder.tvNewsTitle.text = StringUtils.trimToEmpty(titleEn)
	   holder.tvNewsSummary.text = StringUtils.trimToEmpty(summary)
	   holder.tvExtraInf = "难度：$gradeInfo | 单词数：$length | 读后感: $numReviews"
   }
}

// 扩展的类叫 "receiver", 可以为null，如果调用者可能为null,可以这样写：
fun AquariumPlant?.pull() {
   this?. {
   //如果this为null 不执行
       println("removing $this")
       10000
   }
}
val plant: AquariumPlant? = null
plant.pull()
// 无打印
```
| **函数名** | **定义inline的结构**                                         | **函数体内使用的对象** | **返回值** | **适用的场景**           |
| ---------- | ------------------------------------------------------------ | ---------------------- | ---------- | ------------------------ |
| let        | fun <T, R> T.let(block: (T) -> R): R = block(this)           | it                     | 闭包形式   | 判空                     |
| also       | fun T.also(block: (T) -> Unit): T { block(this); return this } | it                     | 返回this   | 增强的let，a=b.also{b=a} |
| with       | fun <T, R> with(receiver: T, block: T.() -> R): R = receiver.block() | this/省略              | 闭包形式   | 同个对象多个方法         |
| run        | fun <T, R> T.run(block: T.() -> R): R = block()              | this/省略              | 闭包形式   | let+with                 |
| apply      | fun T.apply(block: T.() -> Unit): T { block(); return this } | this/省略              | 返回this   | 增强的run,返回this       |

#### 23.扩展函数

```kotlin
open class Animal {
    open fun shout() = "animal is shout"//定义成员函数
}

class Cat: Animal() {
    override fun shout() = "Cat is shout"//子类重写父类成员函数
}

//定义子类和父类扩展函数
fun Animal.eat() = "Animal eat something"
fun Cat.eat()= "Cat eat fish"

//测试
fun main(args: Array<String>) {
    val animal: Animal = Cat()
    println("成员函数测试: ${animal.shout()}")
    println("扩展函数测试: ${animal.eat()}")
}

//成员函数测试: Cat is shout         对于成员函数，实际调用对象是是子类对象
//扩展函数测试: Animal eat something  对于扩展函数，实际调用者是 类 
//本质：扩展函数是 java 中 的 public static final函数
// limitations of extensions ：不能使用private的变量
```

#### 24.Generics 泛型，直译 通用类型

泛型  out in
producer extends,consumer super!



#### 25.伴生对象

```kotlin
	class Activity{
        fun start(){
            pritln("normal fun")
        }
        //伴生对象 ，唯一
        companion object{
            fun tryStaticfun(){
                print("invoke static func")
            }
        }
    }
```

### 26.Unit 表示的是一个值的类型类似void，Nothing表示"永远不存在的值"

```kotlin
EditText().addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) = Unit

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
})
```

### 27.协程

```
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.4"
```

- kotlin协程在jvm本质上就是切线程，靠线程池来实现

- 挂起就是执行完后再切回来

- suspend本身没有实现挂起，只是一个提示

- 非阻塞式：看起来阻塞的代码，做出非阻塞的操作

### 28.LiveData依赖于另一个LiveData 可以使用Transformations.map

```kotlin
// This LiveData depends on another so we can use a transformation.
   val empty: LiveData<Boolean> = Transformations.map(anotherLiveData) {
        it.isEmpty()
    }
```

### 29.