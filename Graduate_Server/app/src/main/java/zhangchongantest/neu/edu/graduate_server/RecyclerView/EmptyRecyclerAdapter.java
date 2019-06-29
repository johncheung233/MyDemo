package zhangchongantest.neu.edu.graduate_server.RecyclerView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Cheung SzeOn on 2019/4/20.
 */

public class EmptyRecyclerAdapter extends RecyclerView{

    private View emptyView;
    public EmptyRecyclerAdapter(Context context) {
        super(context);
    }

    public EmptyRecyclerAdapter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerAdapter(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
        }
    };

    private void checkIfEmpty(){
        Boolean emptyViewVisbility = null;
        if (emptyView != null && getAdapter() !=null){
            emptyViewVisbility = getAdapter().getItemCount() == 0 ?true:false;
            emptyView.setVisibility(emptyViewVisbility?VISIBLE:GONE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter!=null){
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter!=null){
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
