package com.dcu.superword_a_day;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

//import com.dcu.superword_a_day.WordAlternativesDialogFragment.WordAlternativesDialogFragmentListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ResultsActivity extends FragmentActivity implements WordAlternativesDialogFragment.WordAlternativesDialogFragmentListener {
    private ResultsAdapter mAdapter;
    private ViewPager mPager;
    public static ArrayList<String [][]> allRecitalsWithAlternatives;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);        
		try {
			FileInputStream fis = this.getApplicationContext().openFileInput("allRecitalsWithAlternatives");
			ObjectInputStream in = new ObjectInputStream(fis);
			allRecitalsWithAlternatives = (ArrayList<String [][]>)in.readObject();
	        fis.close();
	        in.close();
    	} catch (Exception e){
        	System.out.println("FILE READ EXCEPTION");
    		e.printStackTrace();
    	}
        mAdapter = new ResultsAdapter(getSupportFragmentManager(), this);
        
        mPager = (ViewPager)findViewById(R.id.pager2);
        mPager.setAdapter(mAdapter);
	}

    @Override
    public ResultsFragment getResultsFragment(int position) {
        return (ResultsFragment) mAdapter.getRegisteredFragment(position);
    }

    @Override
    public void onFinishDialog(int result, int fragmentNumber) {
		Log.i("onFinishDialog", "In onFinishDialog");
    	if(result == Constants.SUCCESS_RESULT) {
    		Log.i("onFinishDialog Result", "result code 5 received");
    		
        	ResultsFragment mResultsFragment = (ResultsFragment) mAdapter.getRegisteredFragment(fragmentNumber);
            EditText mEditText = (EditText) mResultsFragment.getView().findViewById(R.id.editText2);
        	mResultsFragment.applySpannableToAll(mEditText);
    	}
    }
    
	public void showResults(View view) {         
		// saving any changes the user may have made to allRecitalsWithAlternatives
		try {
			FileOutputStream fos = openFileOutput("allRecitalsWithAlternatives", Context.MODE_PRIVATE);
	        ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(allRecitalsWithAlternatives);
	        out.flush();
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Intent intent = new Intent(this, AnalyzeResultsActivity.class);
        intent.putExtra("source", Constants.SOURCE_RESULTS_ACTIVITY);
    	startActivity(intent);
	}

}
