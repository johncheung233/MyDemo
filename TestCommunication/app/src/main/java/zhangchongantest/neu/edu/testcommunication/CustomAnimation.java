package zhangchongantest.neu.edu.testcommunication;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class CustomAnimation extends Animation {
    private float centerX;
    private float centerY;
    private int duration;

    public CustomAnimation(float centerX, float centerY, int duration) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.duration = duration;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        setDuration(duration);
        setFillAfter(true);
        setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
    }
}
