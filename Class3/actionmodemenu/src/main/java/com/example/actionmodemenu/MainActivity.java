package com.example.actionmodemenu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String[] names = {"One", "Two", "Three", "Four", "Five"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("names", names[i]);
            list.add(maps);
        }
        //     Context context, List<? extends Map<String, ?>> data,
//            @LayoutRes int resource, String[] from, @IdRes int[] to
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, list,
                R.layout.simple_item, new String[]{"names"}, new int[]{R.id.name});

        ListView listView = findViewById(R.id.listView);

        listView.setAdapter(adapter);


        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int num = 0;
            int blue = Color.parseColor("#00BFFF");

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                if (checked == true) {
                    listView.getChildAt(position).setBackgroundColor(blue);
                    num++;
                } else {
                    listView.getChildAt(position).setBackgroundColor(Color.WHITE);
                    num--;
                }
                mode.setTitle(num + " selected");
            }

            public void deleteAll() {
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.getChildAt(i).setBackgroundColor(Color.WHITE);

                }
                num = 0;
            }

            public void selectAll() {
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.getChildAt(i).setBackgroundColor(blue);
                    listView.setItemChecked(i,true);

                }
                num = listView.getCount();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.delete:
                        deleteAll();
                        mode.finish();
                        return true;
                    case R.id.selected:
                        selectAll();
                        num = listView.getCount();
                        mode.setTitle(num + " selected");
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.action_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                deleteAll();
                mode = null;
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an <code><a href="/reference/android/view/ActionMode.html#invalidate()">invalidate()</a></code> request
                return true;
            }
        });
    }

}