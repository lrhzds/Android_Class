# 实验2_Android界面布局实验
> 第二次作业报告
## 线性布局
> 此题代码在[layout_linear](layout_linear)模块中
- 思路：利用LinearLayout嵌套的方式，在一个 `orientation="vertical"` 的LinearLayout中再嵌套四个 `orientation="horizontal"` 的LinearLayout，将第三行的 `layout_weight` 属性设置为2:2:2:2，其他行都设置为了2:2:3:2。

- 部分关键代码示例：

  activity_main.xml

  ```xml
  <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:background="@color/white"
      android:orientation="vertical">
  
      <LinearLayout
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:orientation="horizontal">
  
          <TextView
              android:layout_width="0dp"
              android:layout_height="wrap_content"
              android:layout_margin="2dp"
              android:layout_weight="2"
              android:background="@color/blue"
              android:gravity="center"
              android:padding="20px"
              android:text="@string/OneThree"
              android:textColor="@color/white" />
  
          <TextView
              android:layout_width="0dp"
              android:layout_height="wrap_content"
              android:layout_margin="2dp"
              android:layout_weight="2"
              android:background="@color/blue"
              android:gravity="center"
              android:padding="20px"
              android:text="@string/OneFour"
              android:textColor="@color/white" />
      </LinearLayout>
  ```

- 线性布局效果如下：

<img src="pic\img.png" width="200px"/>

## 表格布局
> 此题代码在[layout_table1](layout_table1)模块中
- 思路：在外部定义了一个 `TableLayout` , `stretchColumns="1"` ,使得第二列变成可扩展列，就可以通过 `gravity="right"` 使得第二列的组件靠右显示

-  部分关键代码示例：

  activity_main.xml

  ```xml
  <TableLayout xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:background="@color/eblack"
      android:stretchColumns="1">
  
      <TextView
          android:background="@color/gray"
          android:text="Hello  TableLayOut"
          android:textColor="@color/white"
          android:textSize="24dp" />
  
      <TableRow>
  
          <TextView
              android:paddingLeft="20dp"
              android:text="Open..."
              android:textColor="@color/white"
              android:textSize="24dp" />
  
          <TextView
              android:gravity="right"
              android:text="Ctrl-O"
              android:textColor="@color/white"
              android:textSize="24dp" />
      </TableRow>
  ```

- 表格布局效果图如下：
<img src="pic\img_1.png" width="200px">

## 约束布局

1. 
> 此题代码在[layout_constraint](layout_constraint)模块中
- 思路：把控件的样式定义出来后，直接通过控件的拖拽还有设置组件的约束，就可以完成

- 部分关键代码示例：

  ```xml
  <Button
      android:id="@+id/button8"
      android:layout_width="78dp"
      android:layout_height="51dp"
      android:layout_marginStart="20dp"
      android:layout_marginTop="48dp"
      android:backgroundTint="@color/eGray"
      android:text="8"
      android:textColor="@color/black"
      android:textSize="18dp"
      app:layout_constraintStart_toEndOf="@+id/button12"
      app:layout_constraintTop_toBottomOf="@+id/outline" />
  
  <TextView
      android:id="@+id/textView"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_marginStart="28dp"
      android:layout_marginTop="25dp"
      android:text="Input"
      android:textColor="@color/gray"
      android:textSize="30dp"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintTop_toTopOf="parent" />
  ```

- 约束布局第一题效果图如下：
<img src="pic\img_2.png" width="200px"/>

2. 
> 此题代码在[layout_constraint2](layout_constraint2)模块中
- 思路，将图片导入drawable中，通过 `ImageView` 进行调用，One Way那边用到是 `Swith` 控件

- 部分关键代码示例:

  ```xml
  <ImageView
      android:layout_width="14dp"
  
      android:layout_height="44dp"
      android:layout_marginEnd="32dp"
      android:layout_marginBottom="76dp"
      android:src="@drawable/rocket_icon"
      app:layout_constraintBottom_toTopOf="@+id/button"
      app:layout_constraintEnd_toStartOf="@+id/imageView4"
      app:layout_constraintHorizontal_bias="0.991"
      app:layout_constraintStart_toStartOf="parent" />
  
  <Button
      android:id="@+id/button"
      android:layout_width="388dp"
      android:layout_height="47dp"
      android:layout_marginBottom="16dp"
      android:backgroundTint="@color/green"
      android:text="DEPART"
      app:layout_constraintBottom_toBottomOf="parent"
      app:layout_constraintEnd_toEndOf="parent"
      app:layout_constraintHorizontal_bias="0.478"
      app:layout_constraintStart_toStartOf="parent" />
  
  <Switch
      android:id="@+id/switch1"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      app:layout_constraintBottom_toBottomOf="@+id/textView7"
      app:layout_constraintEnd_toEndOf="@+id/textView7"
      app:layout_constraintHorizontal_bias="0.909"
      app:layout_constraintStart_toStartOf="@+id/textView7"
      app:layout_constraintTop_toTopOf="@+id/textView7" />
  ```

- 约束布局第二题效果图如下：
<img src="pic\img_3.png" width="200px"/>