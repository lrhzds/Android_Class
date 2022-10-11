package com.example.actionmodemenu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String[] names = {"One", "Two", "Three", "Four", "Five"};
    private int imageId = R.mipmap.ic_launcher_round;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("names", names[i]);
            listItem.put("pic", imageId);
            list.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.simple_item, new String[]{"names", "pic"}, new int[]{R.id.name, R.id.pic});

        ListView listView = findViewById(R.id.listView);

        listView.setAdapter(simpleAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //从item_menu.xml中构建菜单页面布局
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }
}