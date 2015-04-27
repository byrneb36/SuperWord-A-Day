package com.dcu.superword_a_day;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class MyFragment extends Fragment {
    int mNum;
    private String file_name1;
    private String mSource;
    
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
        if((Constants.SOURCE_WORD_ARCHIVE_ACTIVITY).equals(mSource))
        	file_name1 = Constants.WORD_ARCHIVE_DATA_FILE;
        else if((Constants.SOURCE_TODAYS_REVISION).equals(mSource)) 
        	file_name1 = Constants.REVISION_DATA_FILE;
        else if((Constants.SOURCE_TODAYS_WORDS).equals(mSource))
        	file_name1 = Constants.WORD_DATA_FILE;
        else
        	Log.i("MyFragment", "SOURCE NOT RECOGNIZED");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_fragment, container, false);
        View tv = v.findViewById(R.id.text1);
        
    	final SlidingDrawer s = (SlidingDrawer) v.findViewById(R.id.slidingDrawer1);	
    	
        ((TextView)tv).setText("Fragment #" + mNum);
    	LinkedList<String> wordAndDefinition;
		LinkedList<LinkedList<String>> temp = null;
    	try {
			FileInputStream fis = getActivity().getApplicationContext().openFileInput(file_name1);
			ObjectInputStream in = new ObjectInputStream(fis);
	        temp = (LinkedList<LinkedList<String>>)in.readObject();
	        fis.close();
	        in.close();
    	} catch (Exception e){
        	System.out.println("FILE READ EXCEPTION");
    		e.printStackTrace();
    	}
    	wordAndDefinition = temp.get(mNum);
        
        if(wordAndDefinition != null) {
	        TextView word = (TextView) v.findViewById(R.id.word1);
	        word.setText(wordAndDefinition.get(0).toString());
	        System.out.println("syro 2" + wordAndDefinition.get(0).toString());
	        
	        
	        TextView source = (TextView) v.findViewById(R.id.source1);
	        source.setText(wordAndDefinition.get(1).toString());
	        
	        TextView abbrev = (TextView) v.findViewById(R.id.definition1);
	        for(int i = 2; i < wordAndDefinition.size(); i++) {
	        	System.out.println("syro: " + wordAndDefinition.get(i).toString());
	        	abbrev.append(wordAndDefinition.get(i).toString());
	        }
        }

        
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

            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
                }
            });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);

    }
    
}
