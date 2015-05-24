package com.dcu.superword_a_day;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class WordViewer extends FragmentActivity {

    // checked by MyFragment to see if the broadcast receiver has already received the word percentages
    public static Boolean percentages_received_flag = false;

    public static HashMap<String, String> wordAndDefinitions;
    public static String [] wordArray;
    MyAdapter mAdapter;
    ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_viewer);
		Intent mIntent = getIntent();
		String source = mIntent.getStringExtra("source");
		int numOfWords = mIntent.getIntExtra("numOfWords", -1);
		int itemPosition = mIntent.getIntExtra("itemPosition", -1);
		
        mAdapter = new MyAdapter(getSupportFragmentManager(), this, source, numOfWords);
        
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        
        if((Constants.SOURCE_WORD_ARCHIVE_ACTIVITY).equals(source)) {
        	Log.i("WordViewer", "mPager setting current item to: " + itemPosition);
        	mPager.setCurrentItem(itemPosition);
        }
	}

    
    public void startTest(View view) {
		LinkedList<LinkedList<String>> temp = null;
    	try {
			FileInputStream fis = this.getApplicationContext().openFileInput(Constants.WORD_DATA_FILE);
			ObjectInputStream in = new ObjectInputStream(fis);
	        temp = (LinkedList<LinkedList<String>>)in.readObject();
	        fis.close();
	        in.close();
    	} catch (Exception e){
        	System.out.println("FILE READ EXCEPTION");
    		e.printStackTrace();
    	}
    	
    	// reducing wordArray to the size of sharedPrefNoOfWords
    	SharedPreferences settings = getSharedPreferences(Constants.OPTIONS_FILE, MODE_PRIVATE);
	    int sharedPrefNoOfWords = settings.getInt("numOfWordsPerDay", -1);
    	if(sharedPrefNoOfWords != -1) {
    		wordArray = new String[sharedPrefNoOfWords];
    	}
    	else {
       	 	wordArray = new String[temp.size()];
    	}
    	Log.i("WordViewer sharedPrefNoOfWords: ", "" + sharedPrefNoOfWords);
    	wordAndDefinitions = new HashMap<String, String> ();
    	for(int i = 0; i < wordArray.length; i++) {
        	wordArray[i] = temp.get(i).get(0);
        	String definitions = "";
            for(int j = 2; j < temp.get(i).size(); j++) {
            	definitions = definitions + temp.get(i).get(j).toString();
            }
            wordAndDefinitions.put(wordArray[i], definitions);
    	}
    	Log.i("Word Array", Arrays.toString(wordArray));
    	
    	// randomize the order of the recital words
    	shuffleArray(wordArray);
    	
    	Log.i("Shuffled Word Array", Arrays.toString(wordArray));

    	
    	Intent intent = new Intent(this, TestActivity.class);
    	//intent.putExtra("word array", wordArray);
    	startActivity(intent);
    }
    
    
    // Fisher-Yates shuffle
    private void shuffleArray(String [] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
          int index = rnd.nextInt(i + 1);
          // Simple swap
          String a = ar[index];
          ar[index] = ar[i];
          ar[i] = a;
        }
    }
    
}
