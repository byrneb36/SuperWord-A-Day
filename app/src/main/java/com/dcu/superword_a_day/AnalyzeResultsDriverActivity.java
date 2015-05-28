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
        String[] testWordArray = {"arenicolous", "apricate", "ascesis", "senary", "peripeteia"};
        String[] testDefinitionsArray = {"living growing or burrowing in sand",
                "to bask in the sun to expose to the sun",
                "the practice of severe self discipline or self control also spelled as askesis",
                "relating to the number six having sixth rank having six parts or things",
                "a sudden or unexpected change of fortune especially in a literary work"};
        String[] testRecitalsArray = {"living growing or burrowing in sand",
                "to bask or expose to the sun",
                "the practice of severe self discipline and self control",
                "relating to the number six such as having sixth rank or six parts",
                "a sudden change of fortune"};
        startActivity(testWordArray, testDefinitionsArray, testRecitalsArray);
    }

	public void startTC2(View view) {
        String [] testWordArray = {"adumbrate", "omadhaun", "cruet", "blee", "eldritch"};
        String [] testDefinitionsArray = {"to give a sketchy outline of",
                "a fool or simpleton",
                "a small glass bottle for holding a condiment",
                "color hue complexion",
                "strange or unearthly eerie"};
        String [] testRecitalsArray = {"to give a rough outline of",
                "a fool",
                "a small glass bottle used for holding a condiment",
                "color or hue",
                "strange or eerie"};
        startActivity(testWordArray, testDefinitionsArray, testRecitalsArray);
	}

    // all but the last one are exact matches
    public void startTC3(View view) {
        String [] testWordArray = {"kerplunk", "terpischorean", "amort", "blee", "eldritch"};
        String [] testDefinitionsArray = {"a sound like something heavy falling in water to make such a sound",
                "a dancer related to dancing music or lyrical poetry",
                "lifeless spiritless depressed usually in the phrase all amort",
                "a remarkable or astounding person or thing",
                "a type of fiber made of flax and hemp to drag something along the ground to troll for fish"};
        String [] testRecitalsArray = {"a sound like something heavy falling in water to make such a sound",
                "a dancer related to dancing music or lyrical poetry",
                "lifeless spiritless depressed usually in the phrase all amort",
                "a remarkable or astounding person or thing",
                "a type of fiber made of flax and hemp to drag something along the ground"};
        startActivity(testWordArray, testDefinitionsArray, testRecitalsArray);
    }

    // all are completely wrong except the second-last one
    // includes an empty recital text value
    public void startTC4(View view) {
        String [] testWordArray = {"celadon", "alizarin", "dight", "fomite", "belvedere"};
        String [] testDefinitionsArray = {"a type of pottery having a pale green glaze a pale to very pale green",
                "an orange red crystalline compound used in making dyes",
                "to dress or adorn",
                "an inanimate object or substance capable of transmitting infectious organisms from one individual to another",
                "roofed structure especially a small pavilion or twoer on top of a building situated so as to command a wide view"};
        String [] testRecitalsArray = {"",
                "a dancer related to dancing music or lyrical poetry",
                "to dress or adorn",
                "a remarkable or astounding person or thing",
                "a type of fiber made of flax and hemp to drag something along the ground"};
        startActivity(testWordArray, testDefinitionsArray, testRecitalsArray);
    }

    private void startActivity(String [] testWordArray, String [] testDefinitionsArray, String [] testRecitalsArray) {
        Intent analyzeResultsIntent = new Intent(this, AnalyzeResultsActivity.class);
        analyzeResultsIntent.putExtra("source", Constants.SOURCE_ANALYZE_RESULTS_DRIVER_ACTIVITY);
        analyzeResultsIntent.putExtra("testWordArray", testWordArray);
        analyzeResultsIntent.putExtra("testDefinitionsArray", testDefinitionsArray);
        analyzeResultsIntent.putExtra("testRecitalsArray", testRecitalsArray);
        startActivity(analyzeResultsIntent);
    }
	// must use startactivityforresult()

}
