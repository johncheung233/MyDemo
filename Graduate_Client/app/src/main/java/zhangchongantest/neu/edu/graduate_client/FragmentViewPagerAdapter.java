package zhangchongantest.neu.edu.graduate_client;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_client.Fragment.MainFragment;
import zhangchongantest.neu.edu.graduate_client.Fragment.MineFragment;

/**
 * Created by Cheung SzeOn on 2019/3/30.
 */

public class FragmentViewPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment>fragmentList;
    private final FragmentManager fm;
    private Context context;
    public FragmentViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fm = fm;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}
