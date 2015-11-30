package com.example.slidemenu.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.slidemenu.util.ColorUtil;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by GD-se7en on 2015.11.29.
 */
public class SlideMenu extends FrameLayout{

    private ViewDragHelper viewDragHelper;
    private View menuView;
    private View mainView;
    private int menuWidth;
    private int mainWidth;
    private int width;
    private int dragRange;
    private FloatEvaluator floatEvaluator;

    private DragState mState = DragState.Close;

    public DragState getCurrentState() {
        return mState;
    }

    enum DragState{
        Open,Close
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context) {
        this(context, null);
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2){
            throw new IllegalArgumentException("SlideMenu only can have 2 children!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        menuWidth = menuView.getMeasuredWidth();
        mainWidth = mainView.getMeasuredWidth();
        width = getMeasuredWidth();
        dragRange = (int)(width * 0.6f);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView){
                if (left < 0){
                    left = 0;
                }
                if (left > dragRange){
                    left = dragRange;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menuView){
                menuView.layout(0,0, dragRange,menuView.getMeasuredHeight());
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0)newLeft = 0;
                if (newLeft > dragRange)newLeft = dragRange;
                mainView.layout(newLeft, mainView.getTop(), newLeft+mainWidth, mainView.getBottom());
            }
            float fraction = mainView.getLeft() * 1f / dragRange;
            executeAnim(fraction);
            if (fraction==0 && mState!= DragState.Close){
                mState = DragState.Close;
                if (listener != null){
                    listener.onClose();
                }
            }else if (fraction==1 && mState!= DragState.Open){
                mState = DragState.Open;
                if (listener != null){
                    listener.onOpen();
                }
            }
            if (listener != null){
                listener.onDragging(fraction);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mainView.getLeft() < dragRange/2){
                close();
            }else {
                open();
            }
            if (xvel<-100 && mState!= DragState.Close){
                close();
            }
            if (xvel>100 && mState!= DragState.Open){
                open();
            }
        }

    };

    public void open() {
        viewDragHelper.smoothSlideViewTo(mainView, dragRange, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    private void executeAnim(float fraction) {
        ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction, 1, 0.8));
        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1, 0.8));

        ViewHelper.setTranslationX(menuView, floatEvaluator.evaluate(fraction, -menuWidth / 2, 0));
        ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.5, 1));
        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5, 1));
        ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3, 1));

        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    private OnDragStateChangeListener listener;
    public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
        this.listener = listener;
    }

    public interface OnDragStateChangeListener{
        void onOpen();
        void onDragging(float fraction);
        void onClose();
    }

}
