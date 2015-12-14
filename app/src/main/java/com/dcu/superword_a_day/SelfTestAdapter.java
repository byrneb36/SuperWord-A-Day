package com.dcu.superword_a_day;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by Brendan on 16-Nov-15.
 */
public class SelfTestAdapter extends FragmentPagerAdapter {
    private int num_items;
    private Context c;
    private int sharedPrefNoOfWords;
    private String source;

    public SelfTestAdapter(FragmentManager fm, Context c, int numItems) {
        super(fm);
        Log.i("SelfTestAdapter", "inside SelfTestAdapter");
        this.c = c;
        num_items = numItems;
    }

    @Override
    public int getCount() {
        return num_items;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("SelfTestAdapter", "inside getItem");
        return SelfTestFragment.newInstance(position);
    }
}
