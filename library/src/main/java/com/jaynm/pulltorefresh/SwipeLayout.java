package com.jaynm.pulltorefresh;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

/**
 * @author caobo
 */
public class SwipeLayout extends FrameLayout {

    private static final int CONTENT_VIEW_ID = 1;
    private static final int MENU_VIEW_ID = 2;

    private static final int STATE_CLOSE = 0;
    private static final int STATE_OPEN = 1;
    @SwipeDirection
    private int mSwipeDirection = SwipeMenuListView.DIRECTION_LEFT_TO_RIGHT;

    private View contentView;
    private View menuView;
    private int mDownX;
    private int state = STATE_CLOSE;
    private GestureDetectorCompat flingDetector;
    private boolean isFling;
    private int MIN_FLING = dp2px(15);
    private int MAX_VELOCITYX = -dp2px(500);
    private ScrollerCompat mOpenScroller;
    private ScrollerCompat mCloseScroller;
    private int mBaseX;
    private int position;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;

    private boolean mSwipEnable = true;

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSwipeDirection(int swipeDirection) {
        mSwipeDirection = swipeDirection;
    }

    private void init() {
        initFlingDetector();

        if (mCloseInterpolator != null) {
            mCloseScroller = ScrollerCompat.create(getContext(), mCloseInterpolator);
        } else {
            mCloseScroller = ScrollerCompat.create(getContext());
        }
        if (mOpenInterpolator != null) {
            mOpenScroller = ScrollerCompat.create(getContext(), mOpenInterpolator);
        } else {
            mOpenScroller = ScrollerCompat.create(getContext());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = findViewById(R.id.swipe_layout_content);
        menuView = findViewById(R.id.swipe_layout_menu);
        if (contentView == null) contentView = getChildAt(0);
        if (menuView == null) menuView = getChildAt(1);
    }

    void initFlingDetector() {
        OnGestureListener listener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // TODO
                if (Math.abs(e1.getX() - e2.getX()) > MIN_FLING && velocityX < MAX_VELOCITYX) {
                    isFling = true;
                }
                // Log.i("byz", MAX_VELOCITYX + ", velocityX = " + velocityX);
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
        flingDetector = new GestureDetectorCompat(getContext(), listener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public boolean onSwipe(MotionEvent event) {
        flingDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                isFling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dis = (int) (mDownX - event.getX());
                if (state == STATE_OPEN) {
                    dis += menuView.getWidth() * mSwipeDirection;
                }
                swipe(dis);
                break;
            case MotionEvent.ACTION_UP:
                if (shouldOpen(event)) {
                    smoothOpenMenu();// open
                } else {
                    smoothCloseMenu(); // close
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * right Direction with fling or more than half menu width
     */
    boolean shouldOpen(MotionEvent event) {
        return (isFling || Math.abs(mDownX - event.getX()) > (menuView.getWidth() / 2))
                && Math.signum(mDownX - event.getX()) == mSwipeDirection;
    }

    public boolean isOpen() {
        return state == STATE_OPEN;
    }

    private void swipe(int dis) {
        if (!mSwipEnable) {
            return;
        }
        if (Math.signum(dis) != mSwipeDirection) {
            dis = 0;
        } else if (Math.abs(dis) > menuView.getWidth()) {
            dis = menuView.getWidth() * mSwipeDirection;
        }

        swipeView(contentView, -dis);
        swipeView(menuView, contentView.getWidth() * mSwipeDirection - dis);
    }

    private void swipeView(View view, int toLeft) {
        view.layout(toLeft, view.getTop(), view.getWidth() + toLeft, view.getBottom());
    }

    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (mOpenScroller.computeScrollOffset()) {
                swipe(mOpenScroller.getCurrX() * mSwipeDirection);
                postInvalidate();
            }
        } else {
            if (mCloseScroller.computeScrollOffset()) {
                swipe((mBaseX - mCloseScroller.getCurrX()) * mSwipeDirection);
                postInvalidate();
            }
        }
    }

    public void smoothCloseMenu() {
        state = STATE_CLOSE;
        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT_TO_RIGHT) {
            mBaseX = -contentView.getLeft();
            mCloseScroller.startScroll(0, 0, menuView.getWidth(), 0, 350);
        } else {
            mBaseX = menuView.getRight();
            mCloseScroller.startScroll(0, 0, menuView.getWidth(), 0, 350);
        }
        postInvalidate();
    }

    public void smoothOpenMenu() {
        if (!mSwipEnable) {
            return;
        }
        state = STATE_OPEN;
        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT_TO_RIGHT) {
            mOpenScroller.startScroll(-contentView.getLeft(), 0, menuView.getWidth(), 0, 350);
        } else {
            mOpenScroller.startScroll(contentView.getLeft(), 0, menuView.getWidth(), 0, 350);
        }
        postInvalidate();
    }

    public void closeMenu() {
        if (mCloseScroller.computeScrollOffset()) {
            mCloseScroller.abortAnimation();
        }
        if (state == STATE_OPEN) {
            state = STATE_CLOSE;
            swipe(0);
        }
    }

    public void openMenu() {
        if (!mSwipEnable) {
            return;
        }
        if (state == STATE_CLOSE) {
            state = STATE_OPEN;
            swipe(menuView.getWidth() * mSwipeDirection);
        }
    }

    public View getContentView() {
        return contentView;
    }

    public View getMenuView() {
        return menuView;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentView.layout(0, 0, getMeasuredWidth(), contentView.getMeasuredHeight());

        l = mSwipeDirection == SwipeMenuListView.DIRECTION_RIGHT_TO_LEFT ? -menuView.getMeasuredWidth() : contentView.getMeasuredWidth();
        r = l + menuView.getMeasuredWidth();
        menuView.layout(l, 0, r, menuView.getMeasuredHeight());
    }

    public void setMenuHeight(int measuredHeight) {
        Log.i("byz", "pos = " + position + ", height = " + measuredHeight);
        LayoutParams params = (LayoutParams) menuView.getLayoutParams();
        if (params.height != measuredHeight) {
            params.height = measuredHeight;
            menuView.setLayoutParams(menuView.getLayoutParams());
        }
    }

    public void setSwipEnable(boolean swipEnable) {
        mSwipEnable = swipEnable;
    }

    public boolean getSwipEnable() {
        return mSwipEnable;
    }
}
