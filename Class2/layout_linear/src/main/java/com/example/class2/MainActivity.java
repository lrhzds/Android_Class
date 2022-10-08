package com.example.class2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager mgr=getAssets();//得到AssetManager
        TextView textView = findViewById(R.id.x);
        Typeface tf=Typeface.createFromAsset(mgr, "a.ttf");//根据路径得到Typeface
        textView.setTypeface(tf);//设置字体
    }
}