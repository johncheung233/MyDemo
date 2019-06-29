package zhangchongantest.neu.edu.graduate_server.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zhangchongantest.neu.edu.graduate_server.R;

/**
 * Created by Cheung SzeOn on 2019/4/9.
 */

public class Fragment_2 extends BaseFragment{
    private Boolean isPrepared;
    public Fragment_2(){
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2_layout,null);
        isPrepared = true;
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
    }
}
