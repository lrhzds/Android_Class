package com.example.xmlmenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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

}