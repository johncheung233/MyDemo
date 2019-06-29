package zhangchongantest.neu.edu.graduate_server.Fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Cheung SzeOn on 2019/3/31.
 */

public abstract class BaseFragment extends Fragment {
    protected boolean isVisible;
    public BaseFragment() {
        super();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            isVisible = true;
            onVisible();
        }else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible(){
        lazyLoad();
    }
    protected abstract void lazyLoad();

    protected void onInvisible(){}
}
