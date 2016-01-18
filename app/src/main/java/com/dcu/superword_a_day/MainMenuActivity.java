package com.dcu.superword_a_day;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	private TodaysRevision mTodaysRevision;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
        Intent intent = getIntent();
        if("AnalyzeResultsActivity".equals(intent.getStringExtra("callingActivity"))) {
            // upload results to database
        }

		mTodaysRevision = new TodaysRevision(this);


        Log.i("MainMenu", "test");
	}
	
	// if the archive is empty, don't show the WordArchive button
	@Override
	protected void onResume() {
		super.onResume();
		File file = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.DATES_WITH_WORDS_FILE
				+ ".xml");
		Button b = (Button) findViewById(R.id.wordArchiveButton);
		if(file.length() == 0) {
    		b.setVisibility(View.GONE);
    		Log.i("Main Menu", "Word Archive button set to GONE");
		}
		else {
    		b.setVisibility(View.VISIBLE);
    		Log.i("Main Menu", "Word Archive button set to VISIBLE");
		}

        // if there are no words to revise, don't show the Today's Revision button
        mTodaysRevision.loadRevisionIntervals();
        Button b2 = (Button) findViewById(R.id.todaysRevisionButton);
        if(mTodaysRevision.retrieveFromWordArchive() == false) {
            b2.setVisibility(View.GONE);
            Log.i("Main Menu", "Today's Revision button set to GONE");
        }
        else {
            b2.setVisibility(View.VISIBLE);
            Log.i("Main Menu", "Today's Revision button set to VISIBLE");
        }
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

    public void todaysWords(View view) {
    	Intent intent = new Intent(this, TodaysWords.class);
    	startActivity(intent);
    }
    
    public void todaysRevision(View view) {
        SharedPreferences settings = getSharedPreferences(Constants.OPTIONS_FILE, MODE_PRIVATE);
        boolean revisionSkipCheckbox = settings.getBoolean("revisionSkipCheckbox", false);
        if(revisionSkipCheckbox) {
            Intent intent = new Intent(this, SelfTestActivity.class);
            intent.putExtra("source_file", Constants.SOURCE_TODAYS_REVISION);
            startActivity(intent);
        }
        else
            mTodaysRevision.startWordViewer();
    }
    
    public void wordArchive(View view) {
    	Intent intent = new Intent(this, WordArchiveActivity.class);
    	startActivity(intent);
    }  
    
    public void options(View view) {
    	Intent intent = new Intent(this, OptionsActivity.class);
    	startActivity(intent);
    }
    
    public void analyzeResultsDriver(View view) {
    	Intent intent = new Intent(this, AnalyzeResultsDriverActivity.class);
    	startActivity(intent);
    }
    
    public void wordArchiveDriver(View view) {
    	Intent intent = new Intent(this, WordArchiveDriverActivity.class);
    	startActivity(intent);
    }
    /*
    public void testActivityTestDriver(View view) {
        Intent intent = new Intent(this, TestActivityTestDriver.class);
        startActivity(intent);
    }
    */

}
