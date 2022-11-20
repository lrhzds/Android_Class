# NotePad
> 期中作业

## 时间戳

- 效果图：

<img src="picture\time-text.png" width="250px"/>

- 分析：源程序中已有更新事件的条目，只要把它取出来用就行，然后再进行如下转换，将毫秒数转为上图形式

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

