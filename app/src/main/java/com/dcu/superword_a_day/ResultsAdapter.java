package com.dcu.superword_a_day;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;

public class ResultsAdapter extends FragmentPagerAdapter {
    HashMap<Integer, Fragment> registeredFragments = new HashMap<Integer, Fragment>();
    
	private int num_items;
	private Context c;
	private int sharedPrefNoOfWords;
	
    public ResultsAdapter(FragmentManager fm, Context c) {
        super(fm);
        this.c = c;
        setCount();
    }

    public void setCount() {
	    SharedPreferences settings = c.getSharedPreferences(Constants.OPTIONS_FILE, 0);
	    // if no preference is specified, default to 5
	    sharedPrefNoOfWords = settings.getInt("numOfWordsPerDay", 5);
	    num_items = sharedPrefNoOfWords;
    }
    @Override
    public int getCount() {
        return num_items;
    }

    @Override
    public Fragment getItem(int position) {
        return ResultsFragment.newInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}