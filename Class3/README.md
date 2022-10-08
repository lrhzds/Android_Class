# 实验3_Android界面组件实验

> 第三次作业报告

## Android ListView的用法

- 思路

  1. 先在activity_main.xml里定义一个`ListView`组件，再创建一个simple_item.xml的布局文件用来定义ListView中每一个条目的内容
  2. 在Activity.java里面定义一个简单适配器`SimpleAdapter`，借此将数据和布局文件进行绑定和输出
  3. 最后在Activity.java里面定义一个监听器，当点击某一条目时，将那一条的背景色设为红色

- 关键代码：

- activity_main.xml
  
    ```xml
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/eWhite"
        android:orientation="vertical">
    
        <ListView
            android:id="@+id/list_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:divider="@color/black"
          android:dividerHeight="2dp" />
      ...........
    ```
    
  - simple_item.xml
  
    ```xml
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/list_items"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:background="@color/white">
    
        <TextView
            android:id="@+id/name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:paddingLeft="10dp"
            android:textSize="20dp" />
        ...........
    ```
  
  - Activity.java
  
    ```java
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.simple_item, new String[]{"names", "pic"}, new int[]{R.id.name, R.id.pic});
    
            ListView listView = findViewById(R.id.list_view);
    
            listView.setAdapter(simpleAdapter);
    
            listView.setOnItemClickListener((parent, view, position, id) -> {
                System.out.println(view);
                view.setBackgroundColor(Color.RED);
                Toast.makeText(MainActivity.this, names[position], Toast.LENGTH_SHORT).show();
            });
    ```
    
  - 效果截图
  
    <img src="pic\img.png" width="250px"/>
