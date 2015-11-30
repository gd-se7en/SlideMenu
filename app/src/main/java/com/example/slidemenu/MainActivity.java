package com.example.slidemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.slidemenu.ui.widget.MyLinearLayout;
import com.example.slidemenu.ui.widget.SlideMenu;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SlideMenu slideMenu;
    private ImageView iv_head;
    private ListView menu_listview;
    private ListView main_listview;
    private List<String> strings = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();

    }

    private void initData() {
        for (int i = 1; i < 31; i++) {
            strings.add(i + "");
        }
    }

    private void initView() {
        slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        menu_listview = (ListView) findViewById(R.id.menu_listview);
        main_listview = (ListView) findViewById(R.id.main_listview);
        MyLinearLayout my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
        my_layout.setSildeMenu(slideMenu);

        menu_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, strings));

        main_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings));

        slideMenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
                menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
            }

            @Override
            public void onDragging(float fraction) {
                ViewHelper.setAlpha(iv_head, 1 - fraction);
            }

            @Override
            public void onClose() {
                ViewPropertyAnimator.animate(iv_head).translationXBy(20)
                        .setInterpolator(new CycleInterpolator(10))
                        .setDuration(500)
                        .start();
            }
        });
    }

}
