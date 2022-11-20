/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.notepad;

import com.example.android.notepad.adapter.ExpandableListviewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Displays a list of notes. Will display notes from the {@link Uri}
 * provided in the incoming Intent if there is one, otherwise it defaults to displaying the
 * contents of the {@link NotePadProvider}.
 * <p>
 * NOTE: Notice that the provider operations in this Activity are taking place on the UI thread.
 * This is not a good practice. It is only done here to make the code more readable. A real
 * application should use the {@link android.content.AsyncQueryHandler} or
 * {@link android.os.AsyncTask} object to perform operations asynchronously on a separate thread.
 */
public class NotesList extends ListActivity {
    //侧拉菜单列表部分
    private ExpandableListView expand_list_id;
    private String[] groups = {"主题背景", "暂定", "暂定"};
    private String[][] childs = {{"黑白主题", "浅色主题"}, {"暂定"}, {"暂定"}};


    // For logging and debugging
    private static final String TAG = "NotesList";
    /**
     * The columns needed by the cursor adapter
     */
    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,    //2
    };

    /**
     * The index of the title column
     */
    private static final int COLUMN_INDEX_TITLE = 1;
    private static final int COLUMN_INDEX_MODIFICATION_DATE = 2;
    SharedPreferences.Editor editor;


    /**
     * onCreate is called when Android starts this Activity from scratch.
     */
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        DrawerLayout mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            }
        });
        // The user does not need to hold down the key to use menu shortcuts.
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        editor = getSharedPreferences("bgColor", MODE_PRIVATE).edit();
        SharedPreferences pref = getSharedPreferences("bgColor", MODE_PRIVATE);
        if (pref.getInt("win_rgb", 0) == 0) {
            int win_rgb = Color.rgb(0, 191, 255);
            int dc = R.drawable.color;
            int main_bgColor = Color.rgb(240, 248, 255);
            editor.putInt("win_rgb", win_rgb);
            editor.putInt("dc", dc);
            editor.putInt("main_bgColor", main_bgColor);
            editor.apply();
        }

        int win_rgb = pref.getInt("win_rgb", 0);
        Intent intent = getIntent();
        Window window = this.getWindow();

        window.setStatusBarColor(win_rgb);

        int dc = pref.getInt("dc", 0);
        int main_bgColor = pref.getInt("main_bgColor", 0);
        this.getListView().setBackgroundColor(main_bgColor);
//        StatusBarUtils.setWindowStatusBarColor(NotesList.this, dc);

        ActionBar actionBar = getActionBar();

        Drawable dr = this.getResources().getDrawable(dc);
        actionBar.setBackgroundDrawable(dr);
        /* If no data is given in the Intent that started this Activity, then this Activity
         * was started when the intent filter matched a MAIN action. We should use the default
         * provider URI.
         */
        // Gets the intent that started this Activity.
        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        /*
         * Sets the callback for context menu activation for the ListView. The listener is set
         * to be this Activity. The effect is that context menus are enabled for items in the
         * ListView, and the context menu is handled by a method in NotesList.
         */
        getListView().setOnCreateContextMenuListener(this);
        getListView().setDividerHeight(0);
//        getListView().setBackgroundColor(Color.rgb(240, 248, 255));
        /* Performs a managed query. The Activity handles closing and requerying the cursor
         * when needed.
         *
         * Please see the introductory note about performing provider operations on the UI thread.
         */
        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.
                null,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
        SimpleCursorAdapter adapter = null;
        adapter = x(cursor);
        // Sets the ListView's adapter to be the cursor adapter that was just created.
        setListAdapter(adapter);


    }


    private void initView() {
        expand_list_id = findViewById(R.id.expand_list);
        ExpandableListviewAdapter adapter = new ExpandableListviewAdapter(this, groups, childs);
        expand_list_id.setAdapter(adapter);
        //默认展开第一个数组
        //expand_list_id.expandGroup(0);
        //关闭数组某个数组，可以通过该属性来实现全部展开和只展开一个列表功能
        //expand_list_id.collapseGroup(0);
        expand_list_id.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                return false;
            }
        });
        //子视图的点击事件
        expand_list_id.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(NotesList.this, childs[groupPosition][childPosition], Toast.LENGTH_SHORT).show();

                /**点击黑白主题*/
                if (childs[groupPosition][childPosition] == "黑白主题") {
                    int editTextBgColor = Color.rgb(50, 50, 50);
                    int editTextColor = Color.rgb(255, 255, 255);
                    int main_bgColor = Color.rgb(50, 50, 50);
                    int dc = R.drawable.color1;
                    int itemBgColor = R.drawable.item_bgcolor_black;
                    int win_rgb = Color.rgb(77, 78, 78);
                    SharedPreferences.Editor editor = getSharedPreferences("bgColor", MODE_PRIVATE).edit();
                    editor.putInt("dc", dc);
                    editor.putInt("win_rgb", win_rgb);
                    editor.putInt("editTextBgColor", editTextBgColor);
                    editor.putInt("editTextColor", editTextColor);
                    editor.putInt("itemBgColor", itemBgColor);
                    editor.putInt("main_bgColor", main_bgColor);
                    SharedPreferences pref = getSharedPreferences("bgColor", MODE_PRIVATE);
                    editor.apply();

                    int dc1 = pref.getInt("dc", 0);
                    ActionBar actionBar = getActionBar();
                    Drawable dr = NotesList.this.getResources().getDrawable(dc1);
                    actionBar.setBackgroundDrawable(dr);
                    int win_rgb1 = pref.getInt("win_rgb", 0);
                    Window window = NotesList.this.getWindow();
                    window.setStatusBarColor(win_rgb1);
                    int item_BgColor = pref.getInt("itemBgColor", 0);
//                    Drawable item_bg = NotesList.this.getResources().getDrawable(item_BgColor);
//                    LinearLayout main_list = findViewById(R.id.main_list_item);
                    int main_bgColor1 = pref.getInt("main_bgColor", 0);
                    NotesList.this.getListView().setBackgroundColor(main_bgColor1);
//                    View viewById = findViewById(R.id.main_list_item);
//                    viewById.setBackgroundColor(Color.rgb(33,33,33));
                    FloatingActionButton fab = findViewById(R.id.fab);

                }
                /**点击浅色主题*/
                if (childs[groupPosition][childPosition] == "浅色主题") {
                    int editTextBgColor = Color.rgb(255, 255, 255);
                    int editTextColor = Color.rgb(0, 0, 0);
                    int main_bgColor = Color.rgb(240, 248, 255);
                    int dc = R.drawable.color;
                    int win_rgb = Color.rgb(0, 191, 255);
                    SharedPreferences.Editor editor = getSharedPreferences("bgColor", MODE_PRIVATE).edit();
                    editor.putInt("dc", dc);
                    editor.putInt("win_rgb", win_rgb);
                    editor.putInt("editTextBgColor", editTextBgColor);
                    editor.putInt("editTextColor", editTextColor);
                    editor.putInt("main_bgColor", main_bgColor);
                    SharedPreferences pref = getSharedPreferences("bgColor", MODE_PRIVATE);
                    editor.apply();

                    int dc1 = pref.getInt("dc", 0);
                    ActionBar actionBar = getActionBar();
                    Drawable dr = NotesList.this.getResources().getDrawable(dc1);
                    actionBar.setBackgroundDrawable(dr);

                    int win_rgb1 = pref.getInt("win_rgb", 0);
                    Window window = NotesList.this.getWindow();
                    window.setStatusBarColor(win_rgb1);
                    main_bgColor = pref.getInt("main_bgColor", 0);
                    NotesList.this.getListView().setBackgroundColor(main_bgColor);
                }
                return true;
            }
        });
        //用于当组项折叠时的通知。
        expand_list_id.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });
        //
        //用于当组项折叠时的通知。
        expand_list_id.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences pref = getSharedPreferences("bgColor", MODE_PRIVATE);
        if (pref.getInt("win_rgb", 0) == 0) {
            int win_rgb = Color.rgb(0, 191, 255);
            int dc = R.drawable.color;
            int main_bgColor = Color.rgb(240, 248, 255);
            editor.putInt("win_rgb", win_rgb);
            editor.putInt("dc", dc);
            editor.putInt("main_bgColor", main_bgColor);
            editor.apply();
        }

        int win_rgb = pref.getInt("win_rgb", 0);
        Intent intent = getIntent();
        Window window = this.getWindow();

        window.setStatusBarColor(win_rgb);

        int dc = pref.getInt("dc", 0);
//        StatusBarUtils.setWindowStatusBarColor(NotesList.this, dc);

        ActionBar actionBar = getActionBar();
        Drawable dr = this.getResources().getDrawable(dc);
        actionBar.setBackgroundDrawable(dr);


        int main_bgColor = pref.getInt("main_bgColor", 0);
        this.getListView().setBackgroundColor(main_bgColor);
    }


    public SimpleCursorAdapter x(Cursor cursor) {

        /*
         * The following two arrays create a "map" between columns in the cursor and view IDs
         * for items in the ListView. Each element in the dataColumns array represents
         * a column name; each element in the viewID array represents the ID of a View.
         * The SimpleCursorAdapter maps them in ascending order to determine where each column
         * value will appear in the ListView.
         */

        // The names of the cursor columns to display in the view, initialized to the title column
        String[] dataColumns = {NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE};

        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = {android.R.id.text1, android.R.id.text2};


        // Creates the backing adapter for the ListView.
        SimpleCursorAdapter adapter
                = new SimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs
        );
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
        return adapter;
    }

    /**
     * Called when the user clicks the device's Menu button the first time for
     * this Activity. Android passes in a Menu object that is populated with items.
     * <p>
     * Sets up a menu that provides the Insert option plus a list of alternative actions for
     * this Activity. Other applications that want to handle notes can "register" themselves in
     * Android by providing an intent filter that includes the category ALTERNATIVE and the
     * mimeTYpe NotePad.Notes.CONTENT_TYPE. If they do this, the code in onCreateOptionsMenu()
     * will add the Activity that contains the intent filter to its list of options. In effect,
     * the menu will offer the user other applications that can handle notes.
     *
     * @param menu A Menu object, to which menu items should be added.
     * @return True, always. The menu should be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);

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
                        getIntent().getData(),            // Use the default content URI for the provider.
                        PROJECTION,                       // Return the note ID and title for each note.
                        NotePad.Notes.COLUMN_NAME_TITLE + " Like ?",                             // No where clause, return all records.
                        new String[]{"%" + newText + "%"},                             // No where clause, therefore no where column values.
                        NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
                );
                SimpleCursorAdapter adapter = null;
                adapter = x(cursor);
                // Sets the ListView's adapter to be the cursor adapter that was just created.
                setListAdapter(adapter);
                return true;
            }
        });


//         Generate any additional actions that can be performed on the
//         overall list.  In a normal install, there are no additional
//         actions found here, but this allows other applications to extend
//         our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // The paste menu item is enabled if there is data on the clipboard.
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);


        MenuItem mPasteItem = menu.findItem(R.id.menu_paste);

        // If the clipboard contains an item, enables the Paste option on the menu.
        if (clipboard.hasPrimaryClip()) {
            mPasteItem.setEnabled(true);
        } else {
            // If the clipboard is empty, disables the menu's Paste option.
            mPasteItem.setEnabled(false);
        }

        // Gets the number of notes currently being displayed.
        final boolean haveItems = getListAdapter().getCount() > 0;

        // If there are any notes in the list (which implies that one of
        // them is selected), then we need to generate the actions that
        // can be performed on the current selection.  This will be a combination
        // of our own specific actions along with any extensions that can be
        // found.
        if (haveItems) {

            // This is the selected item.
            Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());

            // Creates an array of Intents with one element. This will be used to send an Intent
            // based on the selected menu item.
            Intent[] specifics = new Intent[1];

            // Sets the Intent in the array to be an EDIT action on the URI of the selected note.
            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);

            // Creates an array of menu items with one element. This will contain the EDIT option.
            MenuItem[] items = new MenuItem[1];

            // Creates an Intent with no specific action, using the URI of the selected note.
            Intent intent = new Intent(null, uri);

            /* Adds the category ALTERNATIVE to the Intent, with the note ID URI as its
             * data. This prepares the Intent as a place to group alternative options in the
             * menu.
             */
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

            /*
             * Add alternatives to the menu
             */
            menu.addIntentOptions(
                    Menu.CATEGORY_ALTERNATIVE,  // Add the Intents as options in the alternatives group.
                    Menu.NONE,                  // A unique item ID is not required.
                    Menu.NONE,                  // The alternatives don't need to be in order.
                    null,                       // The caller's name is not excluded from the group.
                    specifics,                  // These specific options must appear first.
                    intent,                     // These Intent objects map to the options in specifics.
                    Menu.NONE,                  // No flags are required.
                    items                       // The menu items generated from the specifics-to-
                    // Intents mapping
            );
            // If the Edit menu item exists, adds shortcuts for it.
            if (items[0] != null) {

                // Sets the Edit menu item shortcut to numeric "1", letter "e"
                items[0].setShortcut('1', 'e');
            }
        } else {
            // If the list is empty, removes any existing alternative actions from the menu
            menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
        }

        // Displays the menu
        return true;
    }


    /**
     * This method is called when the user selects an option from the menu, but no item
     * in the list is selected. If the option was INSERT, then a new Intent is sent out with action
     * ACTION_INSERT. The data from the incoming Intent is put into the new Intent. In effect,
     * this triggers the NoteEditor activity in the NotePad application.
     * <p>
     * If the item was not INSERT, then most likely it was an alternative option from another
     * application. The parent method is called to process the item.
     *
     * @param item The menu item that was selected by the user
     * @return True, if the INSERT menu item was selected; otherwise, the result of calling
     * the parent method.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:   //返回键的id

                return false;
            case R.id.menu_paste:
                /*
                 * Launches a new Activity using an Intent. The intent filter for the Activity
                 * has to have action ACTION_PASTE. No category is set, so DEFAULT is assumed.
                 * In effect, this starts the NoteEditor Activity in NotePad.
                 */
                startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method is called when the user context-clicks a note in the list. NotesList registers
     * itself as the handler for context menus in its ListView (this is done in onCreate()).
     * <p>
     * The only available options are COPY and DELETE.
     * <p>
     * Context-click is equivalent to long-press.
     *
     * @param menu     A ContexMenu object to which items should be added.
     * @param view     The View for which the context menu is being constructed.
     * @param menuInfo Data associated with view.
     * @throws ClassCastException
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        // Tries to get the position of the item in the ListView that was long-pressed.
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // If the menu object can't be cast, logs an error.
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        /*
         * Gets the data associated with the item at the selected position. getItem() returns
         * whatever the backing adapter of the ListView has associated with the item. In NotesList,
         * the adapter associated all of the data for a note with its list item. As a result,
         * getItem() returns that data as a Cursor.
         */
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);

        // If the cursor is empty, then for some reason the adapter can't get the data from the
        // provider, so returns null to the caller.
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);

        // Sets the menu header to be the title of the selected note.
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

//         Append to the
//         menu items for any other activities that can do stuff with it
//         as well.  This does a query on the system for any activities that
//         implement the ALTERNATIVE_ACTION for our data, adding a menu item
//         for each one that is found.
        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(),
                Integer.toString((int) info.id)));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);
    }

    /**
     * This method is called when the user selects an item from the context menu
     * (see onCreateContextMenu()). The only menu items that are actually handled are DELETE and
     * COPY. Anything else is an alternative option, for which default handling should be done.
     *
     * @param item The selected menu item
     * @return True if the menu item was DELETE, and no default processing is need, otherwise false,
     * which triggers the default handling of the item.
     * @throws ClassCastException
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        /*
         * Gets the extra info from the menu item. When an note in the Notes list is long-pressed, a
         * context menu appears. The menu items for the menu automatically get the data
         * associated with the note that was long-pressed. The data comes from the provider that
         * backs the list.
         *
         * The note's data is passed to the context menu creation routine in a ContextMenuInfo
         * object.
         *
         * When one of the context menu items is clicked, the same data is passed, along with the
         * note ID, to onContextItemSelected() via the item parameter.
         */
        try {
            // Casts the data object in the item into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {

            // If the object can't be cast, logs an error
            Log.e(TAG, "bad menuInfo", e);

            // Triggers default processing of the menu item.
            return false;
        }
        // Appends the selected note's ID to the URI sent with the incoming Intent.
        final Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);

        /*
         * Gets the menu item's ID and compares it to known actions.
         */
        switch (item.getItemId()) {

            case R.id.context_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("是否删除");
                AlertDialog.Builder builder1 = builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(
                                noteUri,  // The URI of the provider
                                null,     // No where clause is needed, since only a single note ID is being
                                // passed in.
                                null      // No where clause is used, so no where arguments are needed.
                        );
                        //Toast.makeText(NoteEditor.this,"选则了取消",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(NoteEditor.this,"选则了取消",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Deletes the note from the provider by passing in a URI in note ID format.
                // Please see the introductory note about performing provider operations on the
                // UI thread.
                // Returns to the caller and skips further processing.
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
     * This method is called when the user clicks a note in the displayed list.
     * <p>
     * This method handles incoming actions of either PICK (get data from the provider) or
     * GET_CONTENT (get or create data). If the incoming action is EDIT, this method sends a
     * new Intent to start NoteEditor.
     *
     * @param l        The ListView that contains the clicked item
     * @param v        The View of the individual item
     * @param position The position of v in the displayed list
     * @param id       The row ID of the clicked item
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        // Gets the action from the incoming Intent
        String action = getIntent().getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
}
