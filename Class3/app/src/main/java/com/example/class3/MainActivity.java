package com.example.class3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private String[] names = {"Lion", "Tiger", "Monkey", "Dog", "Cat", "Elephant"};
    private int[] imageIds = {R.drawable.lion, R.drawable.tiger,
            R.drawable.monkey, R.drawable.dog,
            R.drawable.cat, R.drawable.elephant};

    private Map<View, Boolean> map = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("names", names[i]);
            listItem.put("pic", imageIds[i]);
            list.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.simple_item, new String[]{"names", "pic"}, new int[]{R.id.name, R.id.pic});

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(simpleAdapter);
        // 第六步：listview 的点击事件
        listView.setOnItemClickListener((parent, view, position, id) -> {
            System.out.println(view);
            view.setBackgroundColor(Color.RED);
            Toast.makeText(MainActivity.this, names[position], Toast.LENGTH_SHORT).show();
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(Color.BLUE);
            }
        });
    }




}