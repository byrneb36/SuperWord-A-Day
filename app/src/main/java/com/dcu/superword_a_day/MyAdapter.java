package com.dcu.superword_a_day;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class MyAdapter extends FragmentPagerAdapter {
    
	private int num_items;
	private Context c;
	private int sharedPrefNoOfWords;
	private String source;
	
    public MyAdapter(FragmentManager fm, Context c, String source, int numOfWords) {
        super(fm);
        Log.i("MyAdapter", "inside MyAdapter");
        this.c = c;
        this.source = source;
        if(numOfWords == -1) // this indicates that the source is TodaysWords
        {
        	Log.i("MyAdapter", "numOfWords equals -1");
        	setCount(); 
        }
        else
        	num_items = numOfWords;
    }

    public void setCount() {
    	if((Constants.SOURCE_TODAYS_WORDS).equals(source)) {
    		Log.i("MyAdapter", "inside setCount");
		    SharedPreferences settings = c.getSharedPreferences(Constants.OPTIONS_FILE, 0);
		    // if no preference is specified, default to 5
		    sharedPrefNoOfWords = settings.getInt("numOfWordsPerDay", 5);
		    Log.i("MyAdapter", "sharedPrefNoOfWords: " + sharedPrefNoOfWords);
		    num_items = sharedPrefNoOfWords;
    	}
    }
    @Override
    public int getCount() {
        return num_items;
    }

    @Override
    public Fragment getItem(int position) {
    	Log.i("MyAdapter", "inside getItem");
        return MyFragment.newInstance(position, source);
    }
}
