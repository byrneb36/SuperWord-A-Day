package com.dcu.superword_a_day;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.IllegalFormatCodePointException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class MyFragment extends Fragment {
    private int mNum;
    private String mSource;
    private View my_fragment_view;
    private String mWord, mDefinition1, mDefinition2, mDefSource1, mDefSource2, mExample1, mExampleSource1, mExample2, mExampleSource2,
            mOrigin;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle wordsWithPercentages = intent.getBundleExtra("wordsWithPercentages");
            Log.d("receiver", "Got message: " + wordsWithPercentages.toString());
            WordViewer.percentages_received_flag = true;
            // display the percentage associated with the current fragment
            int wordPercentage = wordsWithPercentages.getInt(mWord);
            ProgressBar percLoadingBar = (ProgressBar) my_fragment_view.findViewById(R.id.percentageLoadingBar);
            percLoadingBar.setVisibility(View.GONE);
            TextView percTextView = (TextView) my_fragment_view.findViewById(R.id.percentageTextView);
            percTextView.setText("" + wordPercentage + "%");
            percTextView.setVisibility(View.VISIBLE);

            // save all percentages to SharedPreferences
            SharedPreferences percentagesFile = getActivity().getApplicationContext()
                    .getSharedPreferences(Constants.DIFFICULTY_PERCENTAGES_FILE, 0);
            SharedPreferences.Editor editor = percentagesFile.edit();
            Set<String> allKeys = wordsWithPercentages.keySet();
            Iterator<String> it = allKeys.iterator();
            String nextKey;
            while(it.hasNext()) {
                nextKey = it.next();
                editor.putInt(nextKey, wordsWithPercentages.getInt(nextKey));
            }
            editor.commit();
        }
    };

    static MyFragment newInstance(int num, String source) {
    	Log.i("MyFragment", "inside MyFragment newinstance");
    	MyFragment f = new MyFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putString("source", source);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        mSource = getArguments().getString("source");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        my_fragment_view = inflater.inflate(R.layout.fragment_my_fragment, container, false);
        View tv = my_fragment_view.findViewById(R.id.text1);
        
    	final SlidingDrawer s = (SlidingDrawer) my_fragment_view.findViewById(R.id.slidingDrawer1);	

    	//LinkedList<String> wordAndDefinition;
        TreeMap fullWordData;

        ((TextView)tv).setText("" + mNum);
        //+ "/" + temp.size());

        /******************* checking to see if the difficulty percentages have already been saved *********************/
        if((Constants.SOURCE_TODAYS_WORDS).equals(mSource)) {
            Log.i("MyFragment", "inside diff perc check");
            if(WordViewer.percentages_received_flag == true) {
                displayPercentage(WordViewer.fullWordDataList);
            }
            else {
                // otherwise, try registering the broadcast receiver
                Log.i("MyFragment", "registering broadcast receiver");
                try {
                    LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mMessageReceiver,
                            new IntentFilter("difficulty-percentages-ready"));
                }
                catch (IllegalArgumentException e) {
                    Log.i("MyFragment", "EXCEPTION: BROADCAST RECEIVER ALREADY REGISTERED");
                }
            }
        }
        else {
            Log.i("MyFragment", "diff perc check: source not today's words");
            File f = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.DIFFICULTY_PERCENTAGES_FILE + ".xml");
            if(f.exists()) {
                displayPercentage(WordViewer.fullWordDataList);
            }
            else {
                // otherwise, just remove the progress bar
                ProgressBar percLoadingBar = (ProgressBar) my_fragment_view.findViewById(R.id.percentageLoadingBar);
                percLoadingBar.setVisibility(View.GONE);
            }
        }

        /***************************************************************************************************************/

    	fullWordData = WordViewer.fullWordDataList.get(mNum);

        // displaying word data
        if(fullWordData != null) {
            TextView wordText = (TextView) my_fragment_view.findViewById(R.id.wordText);
            TextView definition1Text = (TextView) my_fragment_view.findViewById(R.id.definition1Text);
            TextView definitionSource1Text = (TextView) my_fragment_view.findViewById(R.id.definitionSource1Text);
            TextView definition2Text = (TextView) my_fragment_view.findViewById(R.id.definition2Text);
            TextView definitionSource2Text = (TextView) my_fragment_view.findViewById(R.id.definitionSource2Text);
            TextView example1Text = (TextView) my_fragment_view.findViewById(R.id.example1Text);
            TextView exampleSource1Text = (TextView) my_fragment_view.findViewById(R.id.exampleSource1Text);
            TextView example2Text = (TextView) my_fragment_view.findViewById(R.id.example2Text);
            TextView exampleSource2Text = (TextView) my_fragment_view.findViewById(R.id.exampleSource2Text);
            TextView originText = (TextView) my_fragment_view.findViewById(R.id.originText); // i.e. etymology

            mWord = (String) fullWordData.get("word");
            wordText.setText(mWord);
            Log.i("MyFragment", "WORD: " + mWord);
            mDefinition1 = (String) fullWordData.get("definitionSense1");
            SortedMap wordDefs = getByPrefix(fullWordData, "definitionSense1");
            mDefinition1 = (String) wordDefs.remove(wordDefs.firstKey());
            while(!wordDefs.isEmpty()) {
                // create new textviews for additional senses in the definition

                mDefinition1 = mDefinition1 + wordDefs.remove(wordDefs.firstKey()) + "/n";
            }
            definition1Text.setText(mDefinition1);



        }
        /*
        if(wordAndDefinition != null) {
	        TextView word = (TextView) my_fragment_view.findViewById(R.id.word1);
            thisFragmentsWord = wordAndDefinition.get(0).toString();
	        word.setText(thisFragmentsWord);
	        System.out.println("syro 2" + wordAndDefinition.get(0).toString());
	        
	        
	        TextView source = (TextView) my_fragment_view.findViewById(R.id.source1);
	        source.setText(wordAndDefinition.get(1).toString());
	        
	        TextView abbrev = (TextView) my_fragment_view.findViewById(R.id.definition1);
	        for(int i = 2; i < wordAndDefinition.size(); i++) {
	        	System.out.println("syro: " + wordAndDefinition.get(i).toString());
	        	abbrev.append(wordAndDefinition.get(i).toString());
	        }
        }
        */

        // don't allow the user to reveal the sliding drawer "start test" button when they are coming from the Word Archive
        if(!(Constants.SOURCE_WORD_ARCHIVE_ACTIVITY).equals(mSource)) {
            final GestureDetector gesture = new GestureDetector(getActivity(),
                    new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onDown(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                            System.out.println("Syro E1: " +e1+" E2: " +e2);
                            // determine if the intention is to swipe vertically rather than horizontally
                            if(Math.abs(e1.getY() - e2.getY()) > Math.abs(e1.getX() - e2.getX())) {
                                // determine whether it's an up or down swipe
                                if(e2.getY() < e1.getY()) {
                                    s.setVisibility(View.VISIBLE); // set to visible
                                    s.animateOpen();
                                }
                                else {
                                    s.animateClose();
                                }
                                return true;
                            }
                            else {
                                s.setVisibility(View.INVISIBLE); // set to invisible
                                s.close(); // make sure the sliding drawer is always closed when a fragment is swiped to
                                return false;
                            }
                        }
                    });
            my_fragment_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
                }
            });
        }




        return my_fragment_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void displayPercentage(LinkedList<TreeMap> fullWordDataList) {
        SharedPreferences percentagesFile = getActivity().getApplicationContext()
                .getSharedPreferences(Constants.DIFFICULTY_PERCENTAGES_FILE, 0);
        // get the percentage associated with the current fragment's word
        int wordPercentage = percentagesFile.getInt((String) fullWordDataList.get(mNum).get("word"), -1);
        // remove the progress bar and display the percentage if it's available
        ProgressBar percLoadingBar = (ProgressBar) my_fragment_view.findViewById(R.id.percentageLoadingBar);
        percLoadingBar.setVisibility(View.GONE);
        if(wordPercentage != -1) {
            TextView percTextView = (TextView) my_fragment_view.findViewById(R.id.percentageTextView);
            percTextView.setText("" + wordPercentage);
            percTextView.setVisibility(View.VISIBLE);
        }
    }

    private static SortedMap<String, Object> getByPrefix(NavigableMap<String, Object> myMap, String prefix) {
        return myMap.subMap(prefix, prefix + Character.MAX_VALUE);
    }
}
