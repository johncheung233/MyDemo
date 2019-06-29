package zhangchongantest.neu.edu.graduate_server;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Cheung SzeOn on 2019/4/2.
 */

public class CustomImageView extends AppCompatImageView {
    private float width;
    private float height;
    private float radius;
    private Paint paint,borderPaint;
    private Matrix matrix;
    public CustomImageView(Context context) {
        this(context,null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        borderPaint = new Paint();
        paint.setAntiAlias(true); //抗齿距
        matrix = new Matrix();  //缩放矩阵
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        radius = Math.min(width,height)/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (drawable==null){
            super.onDraw(canvas);
            return;
        }
        if (drawable instanceof BitmapDrawable){
            borderPaint.setColor(getResources().getColor(R.color.colorTextViewDefault,null));
            canvas.drawCircle(width/2,height/2,radius+1,borderPaint);
            paint.setShader(initBitmapShader((BitmapDrawable) drawable));//将着色器设置给画笔
            canvas.drawCircle(width / 2, height / 2, radius, paint);//使用画笔在画布上画圆
            return;
        }
        super.onDraw(canvas);
    }

// 获取ImageView中资源图片的Bitmap，利用Bitmap初始化图片着色器,
// 通过缩放矩阵将原资源图片缩放到铺满整个绘制区域，避免边界填充
    private BitmapShader initBitmapShader(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.max(width / bitmap.getWidth()*1f, height / bitmap.getHeight()*1f);
        matrix.set(null);
        float dx =  bitmap.getWidth() / 2;
        float dy = bitmap.getHeight()  / 2;
        matrix.postTranslate(dx, dy);
        matrix.setScale(scale, scale);//将图片宽高等比例缩放，避免拉伸
        bitmapShader.setLocalMatrix(matrix);
        return bitmapShader;
    }
}
