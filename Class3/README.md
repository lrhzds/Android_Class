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





## 创建上下文操作模式(ActionMode)的上下文菜单

> 本题最初不会写，就从网上找了代码来学习和理解，

参考了[csdn](https://blog.csdn.net/qq_39824472/article/details/90549797?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522166567145016782427451318%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=166567145016782427451318&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-1-90549797-null-null.142^v56^pc_rank_34_2,201^v3^control_2&utm_term=listview%E5%88%9B%E5%BB%BAactionmode%E8%8F%9C%E5%8D%95&spm=1018.2226.3001.4187) 和 [developer文档](https://developer.android.google.cn/guide/topics/ui/menus.html#CAB)

- 思路：

  - 先写了一个标题栏的menu，放入了一个打勾和垃圾桶的图片
  - 然后写了一个ListView，重写了适配器，自定义继承了BaseAdapter，将背景色的改变写在了适配器的getView里面，每次选中或取消选中时会调用适配器的notifyDataSetChanged()方法，就会在getView方法中进行判断，当被选中时改变颜色为蓝色，取消选中时变回白色
  - 在MainActivity里面将列表设置为多选模式，然后调用ListView的setMultiChoiceModeListener方法，在里面定义一个num值，随着列表项被选中的数量进行改变，再使用mode.setTitle方法在标题栏进行显示
  - 最后对标题栏的垃圾桶和打勾图片进行处理，当点击以后，会将每个列表项的选中状态置为false，num置为0，再调用notifyDataSetChanged()来刷新，最后再将mode菜单栏关闭

- 对适配器getView方法的理解：其实就是通过setTag为每一个列表项在滚动到的时候设置唯一值，可以起到提高性能的作用，不会导致一下加载过多列表项而影响性能

- 关键代码：

  - ```java
    public View getView(final int position, View convertView, ViewGroup parent) {
    
        final ViewHolder viewHolder;
        //如果还没加载
        if (convertView == null) {
            //加载布局文件，并将各个选项以及每个选项中的内容一一对应
            convertView = View.inflate(context, R.layout.simple_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.pic);
            viewHolder.textView = convertView.findViewById(R.id.name);
            viewHolder.linearLayout = convertView.findViewById(R.id.itemX);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
    
        //得到十六进制的颜色的int值
        int blue = Color.parseColor("#1E90FF");
        int white = Color.parseColor("#FFFFFF");
        viewHolder.textView.setText(list.get(position).getName());
        //如果被选中，那么改变选中颜色
        if (list.get(position).isBo() == true) {
            viewHolder.linearLayout.setBackgroundColor(blue);
        } else {
            viewHolder.linearLayout.setBackgroundColor(white);
        }
        return convertView;
    
    }
    ```

  - ```java
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked == true) {
            list.get(position).setBo(true);
            //实时刷新
            adapter.notifyDataSetChanged();
            num++;
        } else {
            list.get(position).setBo(false);
            //实时刷新
            adapter.notifyDataSetChanged();
            num--;
        }
        // 用TextView显示
        mode.setTitle("  " + num + " Selected");
    }
    ```

- 效果截图：

  <img src="pic\img6.png" width="250px"/>

  点击垃圾桶后：

  <img src="pic\img7.png" width="250px"/>