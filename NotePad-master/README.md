# NotePad

> 期中作业

- 依赖库

```java
dependencies{
        implementation fileTree(dir:'libs',includes:['*.jar'])
        implementation'com.google.android.material:material:1.3.0'
        implementation'de.hdodenhof:circleimageview:2.1.0'
        implementation'androidx.appcompat:appcompat:1.0.0'
        }
```

- [时间戳](#时间戳)
- [查询功能](#查询功能)
- [排序](#排序)
- [UI美化](#UI美化)
    - [悬浮按钮](#FloatingActionButton(悬浮按钮))
    - [主题设置](#主题设置)
    - [其他](#其他)
- [保存图片](#保存图片)

## 时间戳

- 效果图：

<img src="picture\paixu3.png" width="250px"/>

- 分析：源程序中已有更新事件的条目，只要把它取出来用就行，然后再进行如下转换，将毫秒数转为上图形式
- 关键代码：

```java
SimpleCursorAdapter.ViewBinder viewBinder=new SimpleCursorAdapter.ViewBinder(){
@Override
public boolean setViewValue(View view,Cursor cursor,int i){
        if(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE)==i){
        TextView textView1=(TextView)view;
        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd  HH:mm",Locale.CHINA);
        Date date=new Date(cursor.getLong(i)+(long)(8*60*60*1000));
        Log.d("TIME","onCreate10:"+cursor.getLong(i));
        String time=format.format(date);
        Log.d("TIME","onCreate1:"+time);
        textView1.setText(time);
        return true;
        }
        return false;
        }
        };
        adapter.setViewBinder(viewBinder);
```

## 查询功能

- 效果图：

<img src="picture\query.png" width="250px"/>     <img src="picture\query1.png" width="250px"/>

- 分析：
    - 在`ActionBar`上面先放了一个放大镜的图标，使用`setOnQueryTextListener`设置文本监听。重写`onQueryTextChange`
      ，这个的效果是一旦输入内容就会执行里面的内容，而不需要点击确认才开始执行。
    - 这里使用了模糊查询，默认以内容的修改事件降序排列，就是按时间戳的事件排

- 关键代码：

  ```java
  SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
  //搜索图标是否显示在搜索框内
  mSearchView.setIconifiedByDefault(true);
  //设置搜索框展开时是否显示提交按钮，可不显示
  mSearchView.setSubmitButtonEnabled(false);
  //搜索框是否展开，false表示展开
  mSearchView.setIconified(true);
  //获取焦点
  mSearchView.setFocusable(false);
  mSearchView.requestFocusFromTouch();
  mSearchView.setMaxWidth(600);
  //设置提示词
  mSearchView.setQueryHint("请输入关键字");
  // 设置搜索文本监听
  mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      // 当点击搜索按钮时触发该方法
      @Override
      public boolean onQueryTextSubmit(String query) {
          return false;
      }
      // 当搜索内容改变时触发该方法
      @Override
      public boolean onQueryTextChange(String newText) {
          Log.d(TAG, newText);
          Cursor cursor = managedQuery(
                  getIntent().getData(),            
                  PROJECTION, 
                  NotePad.Notes.COLUMN_NAME_TITLE + " Like ?",               
                  new String[]{"%" + newText + "%"},                         
                  NotePad.Notes.DEFAULT_SORT_ORDER  
          );
          SimpleCursorAdapter adapter = null;
          adapter = x(cursor);
          setListAdapter(adapter);
          return true;
      }
  });
  ```



## 排序

- 后面新补充的，该部分比较简单，因此写的比较潦草

- 可以按照修改时间升序和降序两种排列

  

  - 升序

  <img src="picture\paixu2.png" width="250px"/>

  - 降序

  <img src="picture\paixu3.png" width="250px"/>

  ```java
  case R.id.sheng:
      cursor = managedQuery(
              getIntent().getData(),            // Use the default content URI for the provider.
              PROJECTION,                       // Return the note ID and title for each note.
              null,
              null,// No where clause, therefore no where column values.
              NotePad.Notes.NEW_SORT_ORDER  // Use the default sort order.
      );
      adapter = null;
      adapter = x(cursor);
      // Sets the ListView's adapter to be the cursor adapter that was just created.
      setListAdapter(adapter);
      return true;
  case R.id.jiang:
      cursor = managedQuery(
              getIntent().getData(),            // Use the default content URI for the provider.
              PROJECTION,                       // Return the note ID and title for each note.
              null,
              null,// No where clause, therefore no where column values.
              NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
      );
      adapter = null;
      adapter = x(cursor);
      // Sets the ListView's adapter to be the cursor adapter that was just created.
      setListAdapter(adapter);
      return true;
  ```

## UI美化

### listItem样式

<img src="picture\item.png" width="250px"/>

- 背景颜色如下，就可以实现带圆角的背景

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <!--中间的白色实心背景-->
    <solid android:color="#FFFFFF" />
    <!--灰色边框，色值和宽度-->
    <stroke
        android:width="0dp"
        android:color="#999999" />
    <!--圆角角度：左下和右下-->
    <corners
        android:topLeftRadius="10dp"
        android:topRightRadius="10dp"
        android:bottomLeftRadius="10dp"
        android:bottomRightRadius="10dp" />
</shape>
```

### FloatingActionButton(悬浮按钮)

- 我给这两个按钮分别设置了打开侧拉菜单和创建新笔记的功能

<img src="picture\floatingactionbutton.png" width="250px"/>

- 这个按钮可以悬浮的展示在界面的上方，打开软键盘时会自动上移

<img src="picture\query1.png" width="250px"/>

```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton android:id="@+id/fab"
    android:layout_width="wrap_content" android:layout_height="wrap_content"
    android:layout_alignParentRight="true" android:layout_alignParentBottom="true"
    android:layout_margin="16dp" android:elevation="0dp" android:src="@drawable/setting"
    android:theme="@style/Theme.MaterialComponents.Light.NoActionBar" app:borderWidth="0dp"
    app:tint="@null" />
```

### MaterialButton

- 这也是一种悬浮按钮，更方便设置样式

<img src="picture\button.png" width="250px"/>

必须要声明`theme="@style/Theme.MaterialComponents.Light.NoActionBar"`，不然会报错

```xml
<com.google.android.material.button.MaterialButton android:id="@+id/edit_title"
    android:layout_width="150dp" android:layout_height="50dp" android:layout_alignParentRight="true"
    android:layout_alignParentBottom="true" android:layout_marginStart="16dp"
    android:layout_marginTop="16dp" android:layout_marginEnd="16dp"
    android:layout_marginBottom="16dp" android:backgroundTint="@color/Normal"
    android:text="Edit Title" android:textAllCaps="false" android:textColor="@android:color/white"
    android:textSize="18sp" android:theme="@style/Theme.MaterialComponents.Light.NoActionBar"
    app:shapeAppearanceOverlay="@style/tipImageStyle" />
```

- 最后一行是用来设置样式的

### 主题设置

> 我这里以共设置了两个主题样式，分别为黑白主题和浅色主题，通过`SharedPreferences`的形式存储

- 每次restart和create时，都会先调出`sharepreference`里面的内容，不会随着关闭或退出而变会原样

#### 蓝白主题

<img src="picture\paixu3.png" width="250px"/>     <img src="picture\pic1.png" width="250px"/>

#### 黑白主题

<img src="picture\black1.png" width="250px"/>     <img src="picture\black2.png" width="250px"/>

- 我在xml里面给主界面套了一个侧拉菜单,可以通过侧拉和点击下面我设置的`FloatingActionButton`让其出现

  ```java
  fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          mDrawerLayout.openDrawer(Gravity.LEFT);
      }
  });
  ```

    - 然后我用到了`ExpandableListView`，这个主要是用来做侧拉菜单中的二级菜单的，普通的`ListView`很难搞出二级菜单

<img src="picture\right_side.png" width="250px"/>

- 这是侧拉菜单的xml文件，先在外面套一个`androidx.drawerlayout.widget.DrawerLayout`
  ，然后在其里面添加两个布局，第一个布局表示的是主页面，第二个布局放的是侧拉菜单的页面，主页面里面的ListView必须设id为list，不然在activity里面设置setContentView时会显示不出来。

```xml
<androidx.drawerlayout.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:id="@+id/drawerLayout"
    android:layout_width="match_parent" android:layout_height="match_parent"
    android:fitsSystemWindows="true" tools:openDrawer="start">

    <RelativeLayout android:id="@+id/activity_main" android:layout_width="match_parent"
        android:layout_height="match_parent">

        <RelativeLayout android:layout_width="match_parent" android:layout_height="match_parent">

            <com.google.android.material.floatingactionbutton.FloatingActionButton
                android:id="@+id/fab" android:layout_width="wrap_content"
                android:layout_height="wrap_content" android:layout_alignParentRight="true"
                android:layout_alignParentBottom="true" android:layout_margin="16dp"
                android:elevation="0dp" android:src="@drawable/setting"
                android:theme="@style/Theme.MaterialComponents.Light.NoActionBar"
                app:borderWidth="0dp" app:tint="@null" />

            <com.google.android.material.floatingactionbutton.FloatingActionButton
                android:id="@+id/fab2" android:layout_width="wrap_content"
                android:layout_height="wrap_content" android:layout_alignParentRight="true"
                android:layout_alignParentBottom="true" android:layout_marginRight="100dp"
                android:layout_marginBottom="16dp" android:elevation="8dp"
                android:src="@drawable/add"
                android:theme="@style/Theme.MaterialComponents.Light.NoActionBar"
                android:tint="@color/white" app:borderWidth="0dp" app:tint="@null" />

            <ListView android:id="@android:id/list" android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:defaultFocusHighlightEnabled="false"></ListView>

        </RelativeLayout>
    </RelativeLayout>


    <!--    侧拉菜单-->
    <LinearLayout android:layout_width="300dp" android:layout_height="match_parent"
        android:layout_gravity="left">

        <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="match_parent" android:layout_height="match_parent"
            android:layout_gravity="start" android:background="@color/white"
            android:clickable="true" android:focusable="true" android:orientation="vertical">

            <LinearLayout android:layout_width="match_parent" android:layout_height="200dp"
                android:background="@color/time_text" android:gravity="center">

                <ImageView android:layout_width="80dp" android:layout_height="80dp"
                    android:layout_marginTop="40dp" android:src="@drawable/head" />
            </LinearLayout>

            <ExpandableListView android:id="@+id/expand_list" android:layout_width="match_parent"
                android:layout_height="match_parent" android:indicatorLeft="0dp"
                android:indicatorRight="40dp" />
        </LinearLayout>
    </LinearLayout>

</androidx.drawerlayout.widget.DrawerLayout>
```

- 这是主题转换的部分代码

```java
//子视图的点击事件
        expand_list_id.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
@Override
public boolean onChildClick(ExpandableListView expandableListView,View view,int groupPosition,int childPosition,long l){
        Toast.makeText(NotesList.this,childs[groupPosition][childPosition],Toast.LENGTH_SHORT).show();

        /**点击黑白主题*/
        if(childs[groupPosition][childPosition]=="黑白主题"){
        int editTextBgColor=Color.rgb(50,50,50);
        int editTextColor=Color.rgb(255,255,255);
        int main_bgColor=Color.rgb(50,50,50);
        int dc=R.drawable.color1;
        int itemBgColor=R.drawable.item_bgcolor_black;
        int win_rgb=Color.rgb(77,78,78);
        SharedPreferences.Editor editor=getSharedPreferences("bgColor",MODE_PRIVATE).edit();
        editor.putInt("dc",dc);
        editor.putInt("win_rgb",win_rgb);
        editor.putInt("editTextBgColor",editTextBgColor);
        editor.putInt("editTextColor",editTextColor);
        editor.putInt("itemBgColor",itemBgColor);
        editor.putInt("main_bgColor",main_bgColor);
        SharedPreferences pref=getSharedPreferences("bgColor",MODE_PRIVATE);
        editor.apply();

        int dc1=pref.getInt("dc",0);
        ActionBar actionBar=getActionBar();
        Drawable dr=NotesList.this.getResources().getDrawable(dc1);
        actionBar.setBackgroundDrawable(dr);
        int win_rgb1=pref.getInt("win_rgb",0);
        Window window=NotesList.this.getWindow();
        window.setStatusBarColor(win_rgb1);
        int item_BgColor=pref.getInt("itemBgColor",0);
//                    Drawable item_bg = NotesList.this.getResources().getDrawable(item_BgColor);
//                    LinearLayout main_list = findViewById(R.id.main_list_item);
        int main_bgColor1=pref.getInt("main_bgColor",0);
        NotesList.this.getListView().setBackgroundColor(main_bgColor1);
//                    View viewById = findViewById(R.id.main_list_item);
//                    viewById.setBackgroundColor(Color.rgb(33,33,33));
        FloatingActionButton fab=findViewById(R.id.fab);

        }
```

### 其他

#### 确认删除

- 比较简单，不作介绍

<img src="picture\must.png" width="250px"/>

#### 字体大小

- 简单加了个字体的设置，同样使用`sharedpreference`进行保存,设置了三个字体大小

<img src="picture\size1.png" width="200px"/>     <img src="picture\size2.png" width="200px"/>     <img src="picture\size3.png" width="200px"/>     <img src="picture\size4.png" width="200px"/>

## 保存图片

<img src="picture\pic1.png" width="250px"/>

- 点击左上角图片，即可打开选图界面

<img src="picture\pic2.png" width="250px"/>

- 随便选择几个图片

<img src="picture\pic4.png" width="250px"/>

- 插图部分相对复杂，首先在`manifest`里面添加权限

  ```xml
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  ```

- 在`activity`里面，使用`callGallery`调用图库

  ```java
  //region 调用图库
  private void callGallery() {
      Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
      getAlbum.setType("image/*");
      startActivityForResult(getAlbum, IMAGE_CODE);
  }
  ```

- 获取图片Uri

  ```java
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //参考网址：http://blog.csdn.net/abc__d/article/details/51790806
      Bitmap bm = null;
      // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
      ContentResolver resolver = getContentResolver();
      if (requestCode == IMAGE_CODE) {
          try {
              // 获得图片的uri
              Uri originalUri = data.getData();
  
  
              path = getPathByUri4kitkat(NoteEditor.this, originalUri);
  
              insertImg(path);
              Toast.makeText(NoteEditor.this, path, Toast.LENGTH_SHORT).show();
          } catch (Exception e) {
              e.printStackTrace();
              Toast.makeText(this, "图片插入失败", Toast.LENGTH_SHORT).show();
          }
      }
  }
  ```

- 将Uri转为绝对路径

  ```java
  @SuppressLint("NewApi")
  public static String getPathByUri4kitkat(final Context context, final Uri uri) {
      final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
      // DocumentProvider
      if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
          if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
              final String docId = DocumentsContract.getDocumentId(uri);
              final String[] split = docId.split(":");
              final String type = split[0];
              if ("primary".equalsIgnoreCase(type)) {
                  return Environment.getExternalStorageDirectory() + "/" + split[1];
              }
          } else if (isDownloadsDocument(uri)) {// DownloadsProvider
              final String id = DocumentsContract.getDocumentId(uri);
              final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                      Long.valueOf(id));
              return getDataColumn(context, contentUri, null, null);
          } else if (isMediaDocument(uri)) {// MediaProvider
              final String docId = DocumentsContract.getDocumentId(uri);
              final String[] split = docId.split(":");
              final String type = split[0];
              Uri contentUri = null;
              if ("image".equals(type)) {
                  contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
              } else if ("video".equals(type)) {
                  contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
              } else if ("audio".equals(type)) {
                  contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
              }
              final String selection = "_id=?";
              final String[] selectionArgs = new String[]{split[1]};
              return getDataColumn(context, contentUri, selection, selectionArgs);
          }
      } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
          // (and
          // general)
          return getDataColumn(context, uri, null, null);
      } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
          return uri.getPath();
      }
      return null;
  }
  ```

- 在`edittext`里面插入图片地址

  ```java
  private void insertImg(String path) {
      String tagPath = "<img src=\"" + path + "\"/>";//为图片路径加上<img>标签
      Bitmap bitmap = BitmapFactory.decodeFile(path);
  
      if (bitmap != null) {
          SpannableString ss = getBitmapMime(path, tagPath);
  
          insertPhotoToEditText(ss);
          mText.append("\n");
          Log.d("YYPT", mText.getText().toString());
      }
  }
   //region 将图片插入到EditText中
      private void insertPhotoToEditText(SpannableString ss) {
          Editable et = mText.getText();
          int start = mText.getSelectionStart();
          et.insert(start, ss);
          mText.setText(et);
  
  
          mText.setSelection(start + ss.length());
          mText.setFocusableInTouchMode(true);
          mText.setFocusable(true);
  
  
          updateNote(mText.getText().toString(), getTitle().toString());
  
      }
      //endregion
  
      private SpannableString getBitmapMime(String path, String tagPath) {
          SpannableString ss = new SpannableString(tagPath);//这里使用加了<img>标签的图片路径
  
          int width = ScreenUtils.getScreenWidth(this);
          int height = ScreenUtils.getScreenHeight(this);
  
          Bitmap bitmap = ImageUtils.getSmallBitmap(path, width, 480);
          ImageSpan imageSpan = new ImageSpan(this, bitmap);
          ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          return ss;
      }
  ```

- 这个方法将存入到数据库中的字符串进行解析，并通过图片的地址在edittext上面展示图片

  ```java
  private void initContent() {
      //input是获取将被解析的字符串
      String input = note;
  
      //将图片那一串字符串解析出来,即<img src=="xxx" />
      Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
      Matcher m = p.matcher(input);
  
      SpannableString spannable = new SpannableString(input);
      while (m.find()) {
          //Log.d("YYPT_RGX", m.group());
          //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
          String s = m.group();
          int start = m.start();
          int end = m.end();
          //path是去掉<img src=""/>的中间的图片路径
          String path = s.replaceAll("\\<img src=\"|\"\\/>", "").trim();
  
          Bitmap bitmap = ImageUtils.getSmallBitmap(path, 50, 300);
          ImageSpan imageSpan = new ImageSpan(this, bitmap);
          spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
      }
      mText.setText(spannable);
      mText.append("\n");
      updateNote(mText.getText().toString(), getTitle().toString());
  }
  ```





