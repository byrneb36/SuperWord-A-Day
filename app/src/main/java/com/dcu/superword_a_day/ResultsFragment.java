package com.dcu.superword_a_day;

import com.dcu.superword_a_day.WordAlternativesDialogFragment.WordAlternativesDialogFragmentListener;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import java.util.Arrays;

public class ResultsFragment extends Fragment {
    private int mNum;
    private String amendedRecitalText;
    private String [][] thisNewTokensWithAlternatives;
    public String tag;

    //WordData myWordData;
    
    static ResultsFragment newInstance(int num) {
    	ResultsFragment f = new ResultsFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
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
        tag = "fragment" + mNum;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_results, container, false);
        View tv = v.findViewById(R.id.textView1);
        
    	final SlidingDrawer s = (SlidingDrawer) v.findViewById(R.id.slidingDrawer2);	
    	
    	
        ((TextView)tv).setText("Fragment #" + mNum);
        thisNewTokensWithAlternatives = ResultsActivity.allRecitalsWithAlternatives.get(mNum);
        
        amendedRecitalText = "";
        // printing the most likely recital text
        for(int i = 0; i < thisNewTokensWithAlternatives.length; i++) {
        	Log.i("Results Fragment thisNewTokensWithAlternatives[i][0]: ", thisNewTokensWithAlternatives[i][0]);
        	amendedRecitalText = amendedRecitalText + thisNewTokensWithAlternatives[i][0] + " ";
        }
        EditText speechResult = (EditText) v.findViewById(R.id.editText2);
        speechResult.setText(amendedRecitalText);
        applySpannableToAll(speechResult);
        Log.i("ResultsFragment", "past applySpannableToAll");
        
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
    
    public void applySpannableToAll(EditText speechResult) {
    	Log.i("syro", "inside applyspannable");
    	final SpannableString spannable = SpannableString.valueOf(amendedRecitalText);
    	int start, end;
    	Log.i("ResultsFragment", "amendedRecitalText: " + amendedRecitalText);
        Log.i("ResultsFragment" , "thisNewTokensWithAlternatives" + Arrays.deepToString(thisNewTokensWithAlternatives));
    	for(int i = 0; i < thisNewTokensWithAlternatives.length; i++) {
        	Log.i("syro", "inside first applySpannableToAll for loop");
    		// if a token has alternatives
    		if(thisNewTokensWithAlternatives[i].length > 1 && thisNewTokensWithAlternatives[i][1] != null) {
            	Log.i("syro", "inside first applySpannableToAll if statement");
    			
        		start = amendedRecitalText.indexOf(thisNewTokensWithAlternatives[i][0]);
    			// if there are more than one occurrences of the token
    			if(start != amendedRecitalText.lastIndexOf(thisNewTokensWithAlternatives[i][0])) {
    				// find the index of the specific occurrence
        			int indexOfLastSpace = 0;
        			for(int j = 0; j <= i; j++) {
        				indexOfLastSpace = amendedRecitalText.indexOf(" ", indexOfLastSpace);
        			}   
        			start = indexOfLastSpace + 1;
    			}
        		end = start + thisNewTokensWithAlternatives[i][0].length();
                /*
                Log.i("ResultsFragment", "thisNewTokensWithAlternatives before: " + Arrays.deepToString(thisNewTokensWithAlternatives[i]));
                // shortening length of thisNewTokensWithAlternatives[i] to just the number of non-null elements
                for(int j = 0; j < thisNewTokensWithAlternatives[i].length; j++) {
                    if(thisNewTokensWithAlternatives[i][j] == null)
                        thisNewTokensWithAlternatives[i] = Arrays.copyOf(thisNewTokensWithAlternatives[i], j);
                }
                Log.i("ResultsFragment", "thisNewTokensWithAlternatives after: " + Arrays.deepToString(thisNewTokensWithAlternatives[i]));
                */

                final int finalStart = start, finalEnd = end, finalIndex = i;
                final String [][] copyOfTNTWA = new String [thisNewTokensWithAlternatives.length][];
                System.arraycopy(thisNewTokensWithAlternatives, 0, copyOfTNTWA, 0, thisNewTokensWithAlternatives.length);
                final String copyOfART = amendedRecitalText;

                LongClickableSpan mLongClick = new LongClickableSpan() {
                    final int finalStart1 = finalStart, finalEnd1 = finalEnd, finalIndex1 = finalIndex;
                    final String [][] copyOfTNTWA1 = copyOfTNTWA;
                    final String copyOfART1 = copyOfART;
                    final int mNum1 = mNum;
    	            @Override
    	            public void onClick(View editTextView) {
    	            }
    	            @Override
    	            public void onLongClick(View editTextView) {
    	            	Log.i("ResultsFragment", "inside onLongClick");
                        Log.i("ResultsFragment", "copyOfTNTWA1: " + Arrays.deepToString(copyOfTNTWA1));
    	                DialogFragment newFragment = new WordAlternativesDialogFragment();
    	                Bundle args = new Bundle();
    	                args.putStringArray("oneTokenWithAlternatives", copyOfTNTWA1[finalIndex1]);
    	                args.putString("amendedRecitalText", copyOfART1);
    	                args.putInt("indexOfToken", finalIndex1);
    	                args.putInt("start", finalStart1);
    	                args.putInt("end", finalEnd1);
                        Log.i("ResultsFragment", "FragNum RF: " + mNum1);
                        // for testing
                        args.putInt("fragNum", mNum1);


    	                Log.i("ResultsFragment", "before set Arguments");
    	                newFragment.setArguments(args);
    	                Log.i("ResultsFragment", "after set Arguments");
    	                newFragment.show(getFragmentManager(), "amendedRecitalText");
    	                Log.i("ResultsFragment", "after getFragmentManager");
    	            }
    	            
    	            /*
    	            @Override


    	            public void updateDrawState(TextPaint ds) {// override updateDrawState
    	            	   ds.setUnderlineText(false); // set to false to remove underline
    	            	   ds.setColor(Color.BLACK);
    	            }
    	            */
    			};

                Log.i("ResultsFragment", "Before setSpan");
    		  	spannable.setSpan(mLongClick, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Log.i("ResultsFragment", "After setSpan");
    		}
    	}
        Log.i("ResultsFragment", "Before setText");
	  	speechResult.setText(spannable);
	  	speechResult.setMovementMethod(LongClickLinkMovementMethod.getInstance());

    	Log.i("ResultsFragment", "spannable and movement method set");
    	
    }
    
    
    public void setAmendedRecitalText(String newVersion) {
    	amendedRecitalText = newVersion;
    }
    
    public void setNewTokensWithAlternatives(String [] newOneTokenWithAlternatives, int indexOfArray) {
    	Log.i("tag", "inside setNewTokensWithAlternatives");
        //Log.i("ResultsFragment", Arrays.deepToString(thisNewTokensWithAlternatives));
    	thisNewTokensWithAlternatives[indexOfArray] = newOneTokenWithAlternatives; 
    	ResultsActivity.allRecitalsWithAlternatives.set(mNum, thisNewTokensWithAlternatives);
    	
    }
    
}

