package com.dcu.superword_a_day;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;


public class SelfTestResultsActivity extends Activity {
    private int checkbox_array_length;

    public SelfTestResultsActivity() {
        checkbox_array_length = SelfTestActivity.checkbox_array.length;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_test_results);

        int count_of_trues = 0;
        for(int i = 0; i < checkbox_array_length; i++) {
            if(SelfTestActivity.checkbox_array[i])
                count_of_trues++;
        }

        TextView score = (TextView) findViewById(R.id.self_test_results_score);
        score.setText(" " + count_of_trues + "/" + checkbox_array_length);
    }

    // saves each of the words the user failed in the test and exits to the Main Menu
    public void saveResultAndReturn(View view) {
        // saving the words the user has failed so that they can be highlighted in WordArchiveActivity
        SharedPreferences failedWordsStore = getSharedPreferences(Constants.FAILED_WORDS_FILE, MODE_PRIVATE);
        HashSet<String> failedWords = new HashSet<String>();
        failedWords = (HashSet<String>) failedWordsStore.getStringSet("failedWords", failedWords);
        Log.i("STRA", "Failed words BEFORE NEW ADDITIONS: " + failedWords.toString());
        for(int i = 0; i < checkbox_array_length; i++) {
            if(!SelfTestActivity.checkbox_array[i]) {
                failedWords.add(SelfTestActivity.word_array[i]);
            }
        }
        Log.i("STRA", "Failed words AFTER NEW ADDITIONS: " + failedWords.toString());
        SharedPreferences.Editor editor = failedWordsStore.edit();
        editor.putStringSet("failedWords", failedWords);
        editor.commit();

        // returning to the main  menu
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}
