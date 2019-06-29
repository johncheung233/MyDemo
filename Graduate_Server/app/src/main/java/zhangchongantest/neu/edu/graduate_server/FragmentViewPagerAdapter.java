package zhangchongantest.neu.edu.graduate_server;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by Cheung SzeOn on 2019/4/9.
 */

public class FragmentViewPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment>fragmentList;
    private List<String>titleList;
    private List<Integer>badgeCountList;
    private Context context;
    public FragmentViewPagerAdapter(FragmentManager fm,
                                    Context context,
                                    List<Fragment>fragmentList,
                                    List<String>titleList,
                                    List<Integer>badgeCountList) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
        this.titleList = titleList;
        this.badgeCountList = badgeCountList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

//    public View getTabItemView(int tabId){
//        View customView = LayoutInflater.from(context).inflate(R.layout.tab_item,null);
//        ((TextView)customView.findViewById(R.id.text_tabTitle)).setText(titleList.get(tabId));
//        Badge badge = new QBadgeView(context).bindTarget();
//        badge.setBadgeNumber(badgeCountList.get(tabId));
//        badge.setBadgeGravity(Gravity.END | Gravity.TOP);
//        //badge.setOnDragStateChangedListener(this);
//       return null;
//    }
}
