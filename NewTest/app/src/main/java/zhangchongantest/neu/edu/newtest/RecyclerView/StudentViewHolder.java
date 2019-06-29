package zhangchongantest.neu.edu.newtest.RecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import zhangchongantest.neu.edu.newtest.R;

public class StudentViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.textView_studentId)
    TextView tv_studentId;
    @BindView(R.id.textView_studentName)
    TextView tv_studentName;

    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
