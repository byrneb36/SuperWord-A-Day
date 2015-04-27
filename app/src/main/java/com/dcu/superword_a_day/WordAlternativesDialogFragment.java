package com.dcu.superword_a_day;

import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class WordAlternativesDialogFragment extends DialogFragment {

    public interface WordAlternativesDialogFragmentListener {
    	void onFinishDialog(int result, int fragmentNumber);
        ResultsFragment getResultsFragment(int position);
    }
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		final String [] oneTokenWithAlternativesArray = args.getStringArray("oneTokenWithAlternatives");
		final String amendedRecitalText = args.getString("amendedRecitalText");
		final int indexOfToken = args.getInt("indexOfToken");
		final int start = args.getInt("start");
		final int end = args.getInt("end");
        final int fragNum = args.getInt("fragNum");
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.word_alternatives)
	           .setItems(oneTokenWithAlternativesArray, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   // update newTokensWithAlternatives
                       WordAlternativesDialogFragmentListener activity = (WordAlternativesDialogFragmentListener) getActivity();
                       ResultsFragment mParentFragment = (ResultsFragment) activity.getResultsFragment(fragNum);
                       Log.i("WADA", "FragNum: " + fragNum);
                       Log.i("WADA", "mParentFragment tag: " + mParentFragment.tag);
	           		   Log.i("onClick 1 in WordAlternativesDialogFragment: ", Arrays.toString(oneTokenWithAlternativesArray));
	            	   String item = oneTokenWithAlternativesArray[which];
	            	   String original = oneTokenWithAlternativesArray[0];
	            	   oneTokenWithAlternativesArray[0] = item;
	            	   oneTokenWithAlternativesArray[which] = original;
	           		   Log.i("onClick 2 in WordAlternativesDialogFragment: ", Arrays.toString(oneTokenWithAlternativesArray));
                       mParentFragment.setNewTokensWithAlternatives(oneTokenWithAlternativesArray, indexOfToken);
	            	   // update amendedRecitalText
	            	   Log.i("onClick oldAmendedRecitalText", amendedRecitalText);
	            	   String newAmendedRecitalText = amendedRecitalText.substring(0, start) +
	            			   item + amendedRecitalText.substring(end);
	            	   Log.i("onClick newAmendedRecitalText", newAmendedRecitalText);
                       mParentFragment.setAmendedRecitalText(newAmendedRecitalText);

	            	   activity.onFinishDialog(Constants.SUCCESS_RESULT, fragNum);
	            	   dismiss();

	               }
	    });

	    return builder.create();
	}
}

