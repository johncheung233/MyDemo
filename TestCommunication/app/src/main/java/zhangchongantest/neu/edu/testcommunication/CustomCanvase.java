package zhangchongantest.neu.edu.testcommunication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;

public class CustomCanvase {
    public CustomCanvase(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.dog);
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.colorAccent,null));
        paint.setStrokeWidth(10);
        int saved = canvas.saveLayer(new RectF(100,100,200,200),paint);
        canvas.drawBitmap(bitmap,100,100,paint);
        paint.setXfermode(xfermode);
        canvas.drawBitmap(bitmap,150,200,paint);
        paint.setXfermode(null);
        canvas.restoreToCount(saved);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }
}
