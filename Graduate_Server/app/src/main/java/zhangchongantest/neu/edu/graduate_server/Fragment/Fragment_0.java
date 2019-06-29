package zhangchongantest.neu.edu.graduate_server.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zhangchongantest.neu.edu.graduate_server.Activity.MainActivity;
import zhangchongantest.neu.edu.graduate_server.CallBack.FromActivityCallBack;
import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;
import zhangchongantest.neu.edu.graduate_server.R;
import zhangchongantest.neu.edu.graduate_server.RecyclerView.RecyclerViewAdapter;
import zhangchongantest.neu.edu.graduate_server.RecyclerView.RecyclerViewItemDecoration;
import zhangchongantest.neu.edu.graduate_server.RecyclerView.RecyclerViewScrollListener;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/4/9.
 */

public class Fragment_0 extends BaseFragment implements FromActivityCallBack, SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayoutManager layoutManager;
    private Context context;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Boolean isPrepared = false;
    private Boolean isSmoothScroll = false;
    private SwipeRefreshCallBack mRefreshCallBack;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof SwipeRefreshCallBack){
            mRefreshCallBack = (SwipeRefreshCallBack)context;
        }else {
            throw new RuntimeException(context.toString() + " must implement Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_0_layout, null);
        initView(view);
        isPrepared = true;
        return view;
    }

    private void initView(View view) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        layoutManager = new LinearLayoutManager(context);
        recyclerViewAdapter = new RecyclerViewAdapter(context, new ArrayList<ObjectConfig>());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(5));
        recyclerView.addOnScrollListener(new RecyclerViewListener());
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        refreshLayout.setColorSchemeResources(R.color.colorRefreshProgressBar);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        refreshLayout.setRefreshing(true);
        onRefresh();
    }

    public static Fragment_0 newInstance() {
        Fragment_0 fragment = new Fragment_0();
        return fragment;
    }

    @Override
    public void updataList() {
//        List<ObjectConfig> dataList = ConnectManager.getInstance().getResponseWaitingList();
//        recyclerViewAdapter.dataUpdata(dataList);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<ObjectConfig> responseList = ConnectManager.getInstance().getResponseWaitingList();
                List<ObjectConfig> adapterList = recyclerViewAdapter.getDataList();
//                Log.e(Config.TAG,"responseList:"+responseList.size());
//                Log.e(Config.TAG,"adapterList:"+adapterList.size());
//                Log.e(Config.TAG,"isShowViewType:"+recyclerViewAdapter.getShowEmptyView());
                if (responseList.size() != adapterList.size()) {
                    if (adapterList.isEmpty()){
                        recyclerViewAdapter.dataUpdata(responseList);
                    }else {
                        List<ObjectConfig> diffList = jugdeDriffList(responseList, adapterList);
                        recyclerViewAdapter.dataUpdata(diffList);
                    }
                    refreshLayout.setRefreshing(false);
                    mRefreshCallBack.swipeRefreshSuccess();
                    recyclerViewMoveToPosition(recyclerViewAdapter.getDataList().size()-1);
                } else {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(context, R.string.alreadyUpdata, Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);
    }

    private List<ObjectConfig> jugdeDriffList(List<ObjectConfig> responseList, List<ObjectConfig> adapterList) {
        List<ObjectConfig> diff = new ArrayList<>();
        Map<ObjectConfig, Integer> map = new HashMap<ObjectConfig, Integer>(responseList.size());
        for (ObjectConfig objectConfig : responseList) {
            map.put(objectConfig, 1);
        }
        for (ObjectConfig objectConfig : adapterList) {
            if (map.get(objectConfig) != null) {
                map.put(objectConfig, 2);
                continue;
            }
            diff.add(objectConfig);
        }
        for (Map.Entry<ObjectConfig, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        map.clear();
        return diff;
    }

    private void recyclerViewMoveToPosition(int moveToItem){
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        Log.e(Config.TAG,"firstPosition:"+firstPosition+"***lastPosition"+lastPosition);
        if (moveToItem>lastPosition) {
            recyclerView.scrollToPosition(moveToItem);
        }
//        if (moveToItem <= firstPosition){
//            recyclerView.scrollToPosition(moveToItem);
//            isSmoothScroll =true;
//        }else if (moveToItem <= lastPosition){
//            int toTop = recyclerView.getChildAt(moveToItem - firstPosition).getTop();
//            recyclerView.scrollBy(0,toTop);
//        }else {
//            recyclerView.scrollToPosition(moveToItem);
//            isSmoothScroll =true;
//        }
    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isSmoothScroll){
                isSmoothScroll = false;
                int secondTimeCount = recyclerView.getAdapter().getItemCount() - layoutManager.findFirstVisibleItemPosition();
                if (0 <= secondTimeCount && secondTimeCount<recyclerView.getChildCount()) {
                    int secondTimeMove = recyclerView.getChildAt(secondTimeCount).getTop();
                    recyclerView.scrollBy(0,secondTimeMove);
                }
            }
        }
    }

    public interface SwipeRefreshCallBack{
        void swipeRefreshSuccess();
    }

}
