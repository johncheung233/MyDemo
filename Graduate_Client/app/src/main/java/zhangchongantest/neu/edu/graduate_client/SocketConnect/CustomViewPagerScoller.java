package zhangchongantest.neu.edu.graduate_client.SocketConnect;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Cheung SzeOn on 2019/4/4.
 */

public class CustomViewPagerScoller extends Scroller{
    private int mScrollDuration = 2000; // 滑动速度
    public CustomViewPagerScoller(Context context) {
        super(context);
    }

    public CustomViewPagerScoller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    public CustomViewPagerScoller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    public void initViewPagerScroll(ViewPager viewPager) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(viewPager, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getmScrollDuration() {
        return mScrollDuration;
    }

    public void setmScrollDuration(int mScrollDuration) {
        this.mScrollDuration = mScrollDuration;
    }
}
