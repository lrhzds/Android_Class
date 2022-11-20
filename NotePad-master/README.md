# NotePad
> 期中作业

- [时间戳](#时间戳)
- [查询功能](#查询功能)
- [UI美化](#UI美化)
  - [主题](#主题)

## 时间戳

- 效果图：

<img src="picture\time-text.png" width="250px"/>

- 分析：源程序中已有更新事件的条目，只要把它取出来用就行，然后再进行如下转换，将毫秒数转为上图形式
- 关键代码：

```java
SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        if (cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE) == i) {
            TextView textView1 = (TextView) view;
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.CHINA);
            Date date = new Date(cursor.getLong(i) + (long) (8 * 60 * 60 * 1000));
            Log.d("TIME", "onCreate10:" + cursor.getLong(i));
            String time = format.format(date);
            Log.d("TIME", "onCreate1:" + time);
            textView1.setText(time);
            return true;
        }
        return false;
    }
};
adapter.setViewBinder(viewBinder);
```



# 查询功能

- 效果图：

<img src="picture\query1.png" width="250px"/>     <img src="picture\query.png" width="250px"/>

- 分析：
  - 在`ActionBar`上面先放了一个放大镜的图标，使用`setOnQueryTextListener`设置文本监听。重写`onQueryTextChange`，这个的效果是一旦输入内容就会执行里面的内容，而不需要点击确认才开始执行。
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

---

## UI美化

### 主题

> 我这里以共设置了两个主题样式，分别为黑白主题和浅色主题，通过`SharedPreferences`的形式存储

- 每次restart和create时，都会先调出`sharepreference`里面的内容，不会随着关闭或退出而变会原样

#### 蓝白主题

<img src="picture\white1.png" width="250px"/>     <img src="picture\white2.png" width="250px"/>

#### 黑白主题

<img src="picture\black1.png" width="250px"/>     <img src="picture\black2.png" width="250px"/>



- 我在xml里面给主界面套了一个侧拉菜单,可以通过侧拉和点击下面我设置的`FloatingActionButton`让其出现

<img src="picture\right_side.png" width="250px"/>