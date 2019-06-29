package zhangchongantest.neu.edu.newtest.RecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import zhangchongantest.neu.edu.newtest.Database.BaseBean;
import zhangchongantest.neu.edu.newtest.Database.ClassBean;
import zhangchongantest.neu.edu.newtest.Database.StudentBean;
import zhangchongantest.neu.edu.newtest.MainActivity;
import zhangchongantest.neu.edu.newtest.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<? extends BaseBean> mList;
    private LayoutInflater layoutInflater;
    private static Boolean hasEmptyView = false;
    private final int EmptyDataViewType = 0;
    private final int StudentViewType = 1;
    private final int ClassViewType = 2;
    private View emptyView,studentView;

    public RecyclerViewAdapter(Context context, List<? extends BaseBean> list) {
        layoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == EmptyDataViewType){
            View emptyView = layoutInflater.inflate(R.layout.viewholder_empty, viewGroup, false);
            return new EmptyViewHolder(emptyView);
        }else if ( i == StudentViewType){
            View studentView = layoutInflater.inflate(R.layout.viewholder_student,viewGroup,false);
            return new StudentViewHolder(studentView);
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof EmptyViewHolder){

        }else if (viewHolder instanceof StudentViewHolder){
            StudentBean studentBean = (StudentBean) mList.get(i);
            ((StudentViewHolder) viewHolder).tv_studentId.setText(String.valueOf(studentBean.getStudentId()));
            ((StudentViewHolder) viewHolder).tv_studentName.setText(studentBean.getStudentName());
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size()==0){
            hasEmptyView = true;
            return mList.size()+1;
        }
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasEmptyView){
            return EmptyDataViewType;
        }
        if (mList.get(position) instanceof StudentBean){
            return StudentViewType;
        }else {
            return ClassViewType;
        }
    }

    public void updataList(List newDateList){
        if (!newDateList.isEmpty()){
            if (hasEmptyView){
                hasEmptyView = false;
                mList.clear();
                notifyItemRemoved(0);
            }
            int oldPosition = mList.size();
            mList.addAll(oldPosition,newDateList);
            notifyItemRangeInserted(oldPosition,mList.size());
        }
    }
}
