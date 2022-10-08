# 实验3_Android界面组件实验

> 第三次作业报告

## Android ListView的用法

- 思路

  1. 先在activity_main.xml里定义一个`ListView`组件，再创建一个simple_item.xml的布局文件用来定义ListView中每一个条目的内容
  2. 在Activity.java里面定义一个简单适配器`SimpleAdapter`，借此将数据和布局文件进行绑定和输出
  3. 最后在Activity.java里面定义一个监听器，当点击某一条目时，将那一条的背景色设为红色

- 关键代码：

    - activity_main.xml

        ```java
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



## 创建自定义布局的AlertDialog

- 思路

  1. 在上题的activity_main.xml中放一个Button，定义一个监听器，单击Button后执行定义好的`showXml()`方法
  2. 在showXml()中调用AlertDialog.Builder对象上的setView()将布局添加到 AlertDialog。

- 关键代码

  - ```java
    Button button = findViewById(R.id.button);
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showXml();
        }
    });
    ```

  - ```java
    private void showXml() {
        View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
    
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
    
        alertDialog.show();
    }
    ```

- 对话框上方的ANDROID APP的字体我引用了一个从网上导的免费的字体tff包

- 效果截图

  <img src="pic\img2.png" width="250px"/>



## 使用XML定义菜单

- 思路

  1. 在res下创建了一个menu包，在这个包下创建了一个item_menu.xml，在此定义menu样式
  2. 在MainActivity.java重写onCreateOptionsMenu(Menu menu) 方法和onOptionsItemSelected(@NonNull MenuItem item)方法
  3. 在onOptionsItemSelected(@NonNull MenuItem item)方法中调用swich函数，设置点击不同的item时的效果

- 关键代码

  - ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <menu xmlns:android="http://schemas.android.com/apk/res/android">
        <item
            android:id="@+id/font_size"
            android:title="字体大小">
            <menu>
                <item
                    android:id="@+id/small"
                    android:title="小" />
                <item
                    android:id="@+id/normal_size"
                    android:title="中" />
    ```

  - ```java
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //从item_menu.xml中构建菜单页面布局
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        TextView test = findViewById(R.id.test);
        int v = item.getItemId();
        switch (v){
            case R.id.normal_menu:
                Toast.makeText(this, "点击了菜单", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.color_red:
                test.setTextColor(Color.RED);
                return true;
            case R.id.color_black:
                test.setTextColor(Color.BLACK);
                return true;
            case R.id.small:
                test.setTextSize(10);
                return true;
            case R.id.normal_size:
                test.setTextSize(16);
            case R.id.big:
                test.setTextSize(20);
            default:
                break;
        }
    
        return super.onOptionsItemSelected(item);
    }
    ```

- 效果截图：

  <img src="pic\img3.png" width="250px"/> <img src="pic\img4.png" width="250px"/> <img src="pic\img5.png" width="250px"/>

