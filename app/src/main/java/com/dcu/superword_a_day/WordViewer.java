package com.dcu.superword_a_day;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
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

    public static HashMap<String, String> wordsAndDefinitionsMap;
    public static LinkedList<LinkedList<String>> wordsAndDefinitionsList = null;
    public static String [] wordArray;
    MyAdapter mAdapter;
    ViewPager mPager;
    private String source_file;
    private final int SELF_TEST_TYPE = 0;
    private final int SEMANTIC_MATCHING_TYPE = 1;
    private final int SPOKEN_RECITAL_TYPE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_viewer);
		Intent mIntent = getIntent();
		String source = mIntent.getStringExtra("source");
		int numOfWords = mIntent.getIntExtra("numOfWords", -1);
		int itemPosition = mIntent.getIntExtra("itemPosition", -1);

        
        mPager = (ViewPager)findViewById(R.id.pager);
        
        if((Constants.SOURCE_WORD_ARCHIVE_ACTIVITY).equals(source)) {
            Log.i("WordViewer", "mPager setting current item to: " + itemPosition);
            mPager.setCurrentItem(itemPosition);
            source_file = Constants.WORD_ARCHIVE_DATA_FILE;
        }
        else if((Constants.SOURCE_TODAYS_REVISION).equals(source))
            source_file = Constants.REVISION_DATA_FILE;
        else if((Constants.SOURCE_TODAYS_WORDS).equals(source))
            source_file = Constants.WORD_DATA_FILE;
        else
            Log.i("WordViewer", "SOURCE NOT RECOGNIZED");
        Log.i("WordViewer onCreate source_file: ", source_file);

        try {
            FileInputStream fis = this.getApplicationContext().openFileInput(source_file);
            ObjectInputStream in = new ObjectInputStream(fis);
            wordsAndDefinitionsList = (LinkedList<LinkedList<String>>)in.readObject();
            fis.close();
            in.close();
        } catch (Exception e){
            System.out.println("FILE READ EXCEPTION");
            e.printStackTrace();
        }
        mAdapter = new MyAdapter(getSupportFragmentManager(), this, source, numOfWords);
        mPager.setAdapter(mAdapter);
	}

    
    public void startTest(View view) {
    	// reducing wordArray to the size of sharedPrefNoOfWords
    	SharedPreferences settings = getSharedPreferences(Constants.OPTIONS_FILE, MODE_PRIVATE);
	    int sharedPrefNoOfWords = settings.getInt("numOfWordsPerDay", -1);
        int testType = settings.getInt("testType", -1);
    	if(sharedPrefNoOfWords != -1) {
    		wordArray = new String[sharedPrefNoOfWords];
    	}
    	else {
       	 	wordArray = new String[wordsAndDefinitionsList.size()];
    	}
    	Log.i("WordViewer sharedPrefNoOfWords: ", "" + sharedPrefNoOfWords);
    	wordsAndDefinitionsMap = new HashMap<String, String> ();
    	for(int i = 0; i < wordArray.length; i++) {
        	wordArray[i] = wordsAndDefinitionsList.get(i).get(0);
        	String definitions = "";
            for(int j = 2; j < wordsAndDefinitionsList.get(i).size(); j++) {
            	definitions = definitions + wordsAndDefinitionsList.get(i).get(j).toString();
            }
            wordsAndDefinitionsMap.put(wordArray[i], definitions);
    	}
    	Log.i("Word Array", Arrays.toString(wordArray));
    	
    	// randomize the order of the recital words
    	FisherYatesShuffler.shuffleArray(wordArray);
    	
    	Log.i("Shuffled Word Array", Arrays.toString(wordArray));

        Intent intent = null;
    	if(testType == SELF_TEST_TYPE) {
            intent = new Intent(this, SelfTestActivity.class);
            intent.putExtra("source_file", source_file);
        }
        else if(testType == SEMANTIC_MATCHING_TYPE) {}
        //    intent = new Intent(this, SemanticMatchingActivity.class);
    	else if(testType == SPOKEN_RECITAL_TYPE)
            intent = new Intent(this, TestActivity.class);

    	//intent.putExtra("word array", wordArray);
    	startActivity(intent);
    }
}
