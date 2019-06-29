package zhangchongantest.neu.edu.graduate_server.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_server.R;

/**
 * Created by Cheung SzeOn on 2019/4/20.
 */

public class EmptyDataViewHolder extends RecyclerView.ViewHolder{
    public ImageView imageView;
    public TextView tv_item0;
    public EmptyDataViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView)itemView.findViewById(R.id.imageView);
        tv_item0 = (TextView)itemView.findViewById(R.id.textView_item0);
    }
}
