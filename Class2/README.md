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
- 思路：
- 部分关键代码示例：

- 约束布局第一题效果图如下：
<img src="pic\img_2.png" width="200px"/>

2. 
> 此题代码在[layout_constraint2](layout_constraint2)模块中
- 约束布局第二题效果图如下：
<img src="pic\img_3.png" width="200px"/>