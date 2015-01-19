package mgps.app.gpsstatus;

import java.util.ArrayList;

import mgps.app.gpsstatus.TabInfo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;

public class TabsAdapter extends android.support.v4.app.FragmentPagerAdapter
		implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
//	private static android.support.v4.app.FragmentManager mFragmentManager;
	private final Context mContext;
	private final android.support.v7.app.ActionBar mActionBar;
	private final android.support.v4.view.ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	

	public TabsAdapter(android.support.v7.app.ActionBarActivity activity,
			ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mActionBar = activity.getSupportActionBar();
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
		TabInfo info = new TabInfo(clss, args);
		tab.setTag(info.clss.getName());
		tab.setTabListener(this);
		mTabs.add(info);
		mActionBar.addTab(tab);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}
	
	
	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		return Fragment.instantiate(mContext, info.clss.getName(), info.args);
	}
	public Fragment getItemByTag(TabInfo info) {
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i).clss == info.clss) {
				return Fragment.instantiate(mContext,mTabs.get(i).clss.getName(), mTabs.get(i).args);
			}
		}
		return Fragment.instantiate(mContext, info.clss.getName(), info.args);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Object tag = tab.getTag();
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i).clss.getName() == tag) {
				mViewPager.setCurrentItem(i);
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
}
