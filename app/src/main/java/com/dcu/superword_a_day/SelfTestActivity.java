package com.dcu.superword_a_day;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;


public class SelfTestActivity extends FragmentActivity {
    private SelfTestAdapter mAdapter;
    private ViewPager mPager;
    private LinkedList<LinkedList<String>> wordsAndDefinitionsList = null;
    public static HashMap<String, String> wordsAndDefinitionsMap;
    public static String [] word_array;
    public static boolean [] checkbox_array; // true = know, false = don't know

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_test);

        // The ultimate source is either TodaysWords or TodaysRevision. Can also be started by WordViewer.
        Intent startingIntent = getIntent();
        String source_file = startingIntent.getStringExtra("source_file");

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

        word_array = new String[wordsAndDefinitionsList.size()];
        checkbox_array = new boolean[wordsAndDefinitionsList.size()];

        wordsAndDefinitionsMap = new HashMap<String, String>();
        for(int i = 0; i < word_array.length; i++) {
            word_array[i] = wordsAndDefinitionsList.get(i).get(0);
            String definitions = "";
            for(int j = 2; j < wordsAndDefinitionsList.get(i).size(); j++) {
                definitions = definitions + wordsAndDefinitionsList.get(i).get(j).toString();
            }
            wordsAndDefinitionsMap.put(word_array[i], definitions);
        }

        FisherYatesShuffler.shuffleArray(word_array);

        mAdapter = new SelfTestAdapter(getSupportFragmentManager(), this, word_array.length);

        mPager = (ViewPager)findViewById(R.id.pager3);
        mPager.setAdapter(mAdapter);
    }

    public void showResults(View view) {
        System.out.println("SELF TEST RESULTS: " + Arrays.toString(checkbox_array));
        Intent intent = new Intent(this, SelfTestResultsActivity.class);
        startActivity(intent);
    }
}
