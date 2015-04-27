package com.dcu.superword_a_day;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AnalyzeResultsDriverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze_results_driver);
		
	}
	
	public void startTC1(View view) {
		Intent analyzeResultsIntent = new Intent(this, AnalyzeResultsActivity.class);
		String [] testWordArray = {"arenicolous", "apricate", "ascesis", "senary", "peripeteia"};
		String [] testDefinitionsArray = {"living growing or burrowing in sand", 
				"to bask in the sun to expose to the sun", 
				"the practice of severe self discipline or self control also spelled as askesis", 
				"relating to the number six having sixth rank having six parts or things", 
				"a sudden or unexpected change of fortune especially in a literary work"};
		String [] testRecitalsArray = {"living growing or burrowing in sand", 
				"to bask or expose to the sun", 
				"the practice of severe self discipline and self control", 
				"relating to the number six such as having sixth rank or six parts", 
				"a sudden change of fortune"};
        analyzeResultsIntent.putExtra("source", Constants.SOURCE_ANALYZE_RESULTS_DRIVER_ACTIVITY);
		analyzeResultsIntent.putExtra("testWordArray", testWordArray);
		analyzeResultsIntent.putExtra("testDefinitionsArray", testDefinitionsArray);
		analyzeResultsIntent.putExtra("testRecitalsArray", testRecitalsArray);
        startActivity(analyzeResultsIntent);
	}

	public void startTC2(View view) {
		
	}
	// must use startactivityforresult()

}
