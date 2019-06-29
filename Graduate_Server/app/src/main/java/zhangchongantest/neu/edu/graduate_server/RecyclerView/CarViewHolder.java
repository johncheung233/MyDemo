package zhangchongantest.neu.edu.graduate_server.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_server.R;

/**
 * Created by Cheung SzeOn on 2019/4/19.
 */

public class CarViewHolder extends RecyclerView.ViewHolder{
    ImageView imageView;
    TextView tv_item1, tv_item2, tv_item3;
    public CarViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView)itemView.findViewById(R.id.imageView);
        tv_item1 = (TextView)itemView.findViewById(R.id.textView_item1);
        tv_item2 = (TextView)itemView.findViewById(R.id.textView_item2);
        tv_item3 = (TextView)itemView.findViewById(R.id.textView_item3);
    }
}
