package zhangchongantest.neu.edu.graduate_server.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Cheung SzeOn on 2019/4/22.
 */

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration{
    private int itemDecoration;

    public RecyclerViewItemDecoration(int itemDecoration) {
        this.itemDecoration = itemDecoration;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == 0){
            outRect.top = itemDecoration;
        }
        outRect.left = itemDecoration;
        outRect.right = itemDecoration;
        outRect.bottom = itemDecoration;
    }

}
