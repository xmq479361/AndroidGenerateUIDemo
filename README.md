

### 本例子实现Android Activity布局和事件绑定。主要演示Android项目中，目前代码生成注入的几种实现方案。


### 1. 通过APT生成代理模板类
步骤:
#### 1).在各个需要生成的module分别引入APTDelegate库
```groovy
annotationProcessor project(":APTDelegate:Compiler")
```
#### 2).在Activity中定义需要的注解处理
布局绑定
```java
/** 布局绑定 */
@XBindLayout(R.layout.activity_second)
public class SecondActivity ...
```
UI控件绑定
```java
/** UI控件绑定 */
@XBindView(R.id.second_text)
TextView second_text;
```
UI控件事件绑定
```java
@XBindEvent(value = R.id.second_text, type = EventType.Click)
public void onTextClick(View view) {
}
```
#### 3).在build过当前Project后，Activity中引入生成的模板
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new _SecondActivity$$IXBinder().xbind(this);
}
```

### 2. 在已有模板的基础上，通过AutoService注解实现SPI方式动态注入
实现步骤:
#### 1).引入依赖库
```groovy
// 注入逻辑核心库
implementation project(":AutoDelegate:XBindCore")
// autoService注解处理APT
annotationProcessor 'com.google.auto.service:auto-service:1.0-rc6'
```
#### 2).在bActivity或模板Activity基类中，调用通用处理API
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    XBinder.bind(this);
}
```

### 3. 参考ARouter方式，通过gradle-plugin注入register代码
实现步骤:
#### 1).引入依赖库
```groovy
// 注入逻辑核心库
implementation project(":AutoDelegate:XBindCore")
```
#### 2).在bActivity或模板Activity基类中，调用通用处理API
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    XBinder.bind(this);
}
```


