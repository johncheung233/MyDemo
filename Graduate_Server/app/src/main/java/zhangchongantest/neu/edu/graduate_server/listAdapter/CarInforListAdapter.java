package zhangchongantest.neu.edu.graduate_server.listAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_server.R;

/**
 * Created by Cheung SzeOn on 2018/12/4.
 */

public class CarInforListAdapter extends CursorAdapter{
    private final int mResourceId;
    public CarInforListAdapter(Context context, int mResourceId, Cursor cursor) {
        super(context,cursor,true);
        this.mResourceId = mResourceId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(mResourceId,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_item0 = (TextView)view.findViewById(R.id.textView_item0);
        TextView tv_item1 = (TextView)view.findViewById(R.id.textView_item1);
        TextView tv_item2 = (TextView)view.findViewById(R.id.textView_item2);
        TextView tv_item3 = (TextView)view.findViewById(R.id.textView_item3);
        TextView tv_item4 = (TextView)view.findViewById(R.id.textView_item4);
        TextView tv_item5 = (TextView)view.findViewById(R.id.textView_item5);
        TextView tv_item6 = (TextView)view.findViewById(R.id.textView_item6);
        TextView tv_item7 = (TextView)view.findViewById(R.id.textView_item7);
        tv_item0.setText(cursor.getString(1));
        tv_item1.setText(cursor.getString(2));
        tv_item2.setText(cursor.getString(3));
        tv_item3.setText(cursor.getString(5)+"号车位");
        tv_item4.setText(cursor.getString(8));
        tv_item5.setText("状态："+cursor.getString(6));
        tv_item6.setText(cursor.getString(4));
        tv_item7.setText(cursor.getString(7)+"元");
    }
}
