package zhangchongantest.neu.edu.graduate_test_sockt;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cheung SzeOn on 2018/12/4.
 */

public class NewDeviceAdapter extends ArrayAdapter{
    private final int mResourceId;
    public NewDeviceAdapter(Context context, int resource, List<BlueToothDeviceParce> objects) {
        super(context, resource, objects);
        mResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BlueToothDeviceParce deviceParce = (BlueToothDeviceParce)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(mResourceId,null);
        TextView tv_name = (TextView)view.findViewById(R.id.textview_name);
        TextView tv_address = (TextView)view.findViewById(R.id.textview_address);
        if (!TextUtils.isEmpty(deviceParce.getName())){
            tv_name.setText(deviceParce.getName());
        }
        if (!TextUtils.isEmpty(deviceParce.getAddress())){
            tv_address.setText(deviceParce.getAddress());
        }
        return view;
    }
}
