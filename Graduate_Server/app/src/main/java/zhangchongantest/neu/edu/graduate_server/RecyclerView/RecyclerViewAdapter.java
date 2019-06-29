package zhangchongantest.neu.edu.graduate_server.RecyclerView;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;
import zhangchongantest.neu.edu.graduate_server.R;

/**
 * Created by Cheung SzeOn on 2019/4/19.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int AccountViewType = 1;
    private final int CarViewType = 2;
    private final int ParkingDeviceViewType = 3;
    private final int EmptyDataViewType = 4;

    private boolean isShowEmptyView = false;
    private boolean isBottomView = false;

    private LayoutInflater layoutInflater;
    private List<ObjectConfig> dataList;
    private Context context;
    public RecyclerViewAdapter(Context context, List<ObjectConfig> dataList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AccountViewType){
            View view = layoutInflater.inflate(R.layout.viewholder_account_type,parent,false);
            return new AccountViewHolder(view);
        }else if (viewType == CarViewType){
            View view = layoutInflater.inflate(R.layout.viewholder_account_type,parent,false);
            return new CarViewHolder(view);
        }else if (viewType == ParkingDeviceViewType){
            View view = layoutInflater.inflate(R.layout.viewholder_account_type,parent,false);
            return new ParkingDeviceViewHolder(view);
        }else if (viewType == EmptyDataViewType){
            View view = layoutInflater.inflate(R.layout.viewholder_emptydata_type,parent,false);
            return new EmptyDataViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String action = null;
        if (!dataList.isEmpty()) {
            switch (Integer.parseInt(dataList.get(position).getCmd())) {
                case Config.CMD_CAR_IN:
                    action = "车辆入场";
                    break;
                case Config.CMD_CAR_OUT:
                    action = "车辆离场";
                    break;
                case Config.CMD_PRE_OUT:
                    action = "车辆获取停车费";
                    break;
                case Config.CMD_REGISTER_IN:
                    action = "用户注册";
                    break;
                case Config.CMD_REGISTER_LOGIN:
                    action = "用户登录";
                    break;
                case Config.CMD_REGISTER_LOGOUT:
                    action = "用户注销";
                    break;
                case Config.CMD_PARKING_CHECK:
                    action = "车位绑定";
                    break;
                case Config.CMD_INIT_CHECK:
                    action = "初始化获取";
                    break;
            }
        }
        if (holder instanceof AccountViewHolder){
            ((AccountViewHolder)holder).imageView.setImageResource(R.mipmap.server_user);
            ((AccountViewHolder)holder).tv_item1.setText(dataList.get(position).getAccountName());
            ((AccountViewHolder)holder).tv_item2.setText(action);
            ((AccountViewHolder)holder).tv_item3.setText(dataList.get(position).getSocketType());
        }else if (holder instanceof CarViewHolder){
            ((CarViewHolder)holder).imageView.setImageResource(R.mipmap.server_car);
            ((CarViewHolder)holder).tv_item1.setText(dataList.get(position).getCarID());
            ((CarViewHolder)holder).tv_item2.setText(action);
            ((CarViewHolder)holder).tv_item3.setText(dataList.get(position).getSocketType());
        }else if (holder instanceof ParkingDeviceViewHolder){
            ((ParkingDeviceViewHolder)holder).imageView.setImageResource(R.mipmap.server_device);
            ((ParkingDeviceViewHolder)holder).tv_item1.setText(dataList.get(position).getBookingSpaceId());
            ((ParkingDeviceViewHolder)holder).tv_item2.setText(dataList.get(position).getGetSocketIP().getHostAddress());
            ((ParkingDeviceViewHolder)holder).tv_item3.setText(dataList.get(position).getSocketType());
        }else if (holder instanceof EmptyDataViewHolder){
            ((EmptyDataViewHolder)holder).imageView.setImageResource(R.mipmap.server_emptydata);
            ((EmptyDataViewHolder)holder).tv_item0.setText(R.string.empty_request_data);
        }
    }

    @Override
    public int getItemCount() {
        if (dataList.isEmpty()){
            isShowEmptyView = true;
            return dataList.size()+1;
        }
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowEmptyView==true){
            return EmptyDataViewType;
        }
        int itemViewType=0;
        int cmd = Integer.parseInt(dataList.get(position).getCmd());
        switch (cmd){
            case Config.CMD_CAR_IN:
            case Config.CMD_PRE_OUT:
            case Config.CMD_CAR_OUT:
                itemViewType = CarViewType;
                break;
            case Config.CMD_REGISTER_LOGIN:
            case Config.CMD_REGISTER_LOGOUT:
            case Config.CMD_REGISTER_IN:
            case Config.CMD_INIT_CHECK:
                itemViewType = AccountViewType;
                break;
            case Config.CMD_PARKING_CHECK:
                itemViewType = ParkingDeviceViewType;
                break;
        }
        return itemViewType;
    }

    public void dataUpdata(List<ObjectConfig> updataList){
        //notifyDataSetChanged();
//        if (!dataList.isEmpty()&&isShowEmptyView){
//            int size = dataList.size();
//            dataList.clear();
//            notifyItemRangeRemoved(0, size);
//        }
        if (!updataList.isEmpty()){
            if (isShowEmptyView==true){
                isShowEmptyView = false;
                dataList.clear();
                notifyItemRemoved(0);
            }
            int oldPosition = dataList.size();
            dataList.addAll(dataList.size(),updataList);
            notifyItemRangeInserted(oldPosition, updataList.size());
        }
//        else {
//            if (dataList.isEmpty()) {
//                if (showEmptyView != true) {
//                    showEmptyView = true;
//                    notifyItemInserted(0);
//                }
//            }
//        }
    }

    public List<ObjectConfig> getDataList() {
        return dataList;
    }

    public boolean getShowEmptyView() {
        return isShowEmptyView;
    }

    public void setEmptyView(){
        if (dataList.size()!=0){
            dataList.clear();
            notifyItemRangeRemoved(0,dataList.size());
        }
        if (isShowEmptyView!=true){
            isShowEmptyView=true;
            notifyItemInserted(0);
        }
    }
}
