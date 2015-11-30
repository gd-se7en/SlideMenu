package com.example.slidemenu.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by GD-se7en on 2015.11.30.
 */
public class MyLinearLayout extends LinearLayout {

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLinearLayout(Context context) {
        this(context, null);
    }

    private SlideMenu slideMenu;
    public void setSildeMenu(SlideMenu sildeMenu){
        this.slideMenu = sildeMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSlideMenuOpen()){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSlideMenuOpen()){
            if (event.getAction() == MotionEvent.ACTION_UP){
                slideMenu.close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public boolean isSlideMenuOpen() {
        return slideMenu != null && slideMenu.getCurrentState() == SlideMenu.DragState.Open;
    }
}
