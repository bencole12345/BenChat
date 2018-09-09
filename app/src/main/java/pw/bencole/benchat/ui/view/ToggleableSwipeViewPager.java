package pw.bencole.benchat.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;


/**
 * Functions like a ViewPager, except you can disable swiping between pages.
 *
 * @author Ben Cole
 */
public class ToggleableSwipeViewPager extends ViewPager {

    /**
     * Whether or not swiping is allowed
     */
    private boolean mAllowSwiping = true;

    public ToggleableSwipeViewPager(Context context) {
        super(context);
    }

    public ToggleableSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAllowSwiping(boolean swipingAllowed) {
        mAllowSwiping = swipingAllowed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mAllowSwiping && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mAllowSwiping && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return mAllowSwiping && super.canScrollHorizontally(direction);
    }

    /**
     * Protect against arrow keys on physical or (on some phones) virtual keyboards.
     */
    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        return mAllowSwiping && super.executeKeyEvent(event);
    }

}
