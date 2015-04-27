package com.dcu.superword_a_day;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
 
public class TestActivity extends FragmentActivity {

 
    private ImageButton speakButton;
    private TextView speechResult;
    private String textOfRecital = "null";
    private String [][] newTokensWithAlternatives;
    private ArrayList<String [][]> allRecitalsWithAlternatives;
    private String amendedRecitalText;
	private int wordArrayIndex;
	private int wordArrayLength = -1;
	//private String [] wordArray;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

    	//Intent startingIntent = getIntent();
    	//wordArray = startingIntent.getStringArrayExtra("word array");
    	wordArrayLength = WordViewer.wordArray.length;
    	
    	// if recreating TestActivity
    	if (savedInstanceState != null) {
            wordArrayIndex = savedInstanceState.getInt("wordArrayIndex");    	
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
        }
    	else {
    		wordArrayIndex = 0;
    		allRecitalsWithAlternatives = new ArrayList<String [][]>();
    	}
    	
    	TextView recitalWord = (TextView) findViewById(R.id.textView2);
    	recitalWord.setText(WordViewer.wordArray[wordArrayIndex]);
    	
        speechResult = (TextView) findViewById(R.id.textView1);
 
        speakButton = (ImageButton) findViewById(R.id.speakButton);
 
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
 
                try {
                    startActivityForResult(intent, Constants.RESULT_SPEECH);
                    speechResult.setText("");

                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Your device doesn't support speech-to-text.",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	 savedInstanceState.putInt("wordArrayIndex", wordArrayIndex);
    	 super.onSaveInstanceState(savedInstanceState);
    }
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "INSIDE ONACTIVITYRESULT");
        switch (requestCode) {
        case Constants.RESULT_SPEECH: {
            if (resultCode == RESULT_OK && null != data) {
                String originalDefinition = WordViewer.wordAndDefinitions.get(WordViewer.wordArray[wordArrayIndex]);
                String [] tempOriginalTokens = originalDefinition.split("\\W");
                ArrayList<String> tempArrayListOfTokens = new ArrayList<String>();
                System.out.println("syro tempOriginalTokens: ");
                System.out.println("syro tempOriginalTokens.length: " + tempOriginalTokens.length);
                // eliminating empty tokens
                for(int i = 0; i < tempOriginalTokens.length; i++) {
                	System.out.print(" " + tempOriginalTokens[i]);
                	if(tempOriginalTokens[i].length() > 0) {
                		tempArrayListOfTokens.add((String) tempOriginalTokens[i]);
                	}
                }
            	String [] originalDefinitionTokens = tempArrayListOfTokens.toArray(new String[tempArrayListOfTokens.size()]);
            	System.out.println("TEMPARRAYLISTOFTOKENS: " + tempArrayListOfTokens.toString());

            	ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                textOfRecital = text.get(0);
                String [] recitalTokens = textOfRecital.split("\\W");
                System.out.println("\nsyro recitalTokens: ");
                for(int i = 0; i < recitalTokens.length; i++) {
                	System.out.print(" " + recitalTokens[i]);
                }
                
               // String [] newTokens;
                //newTokens = new String [recitalTokens.length];
                newTokensWithAlternatives = new String[recitalTokens.length][Constants.MAX_NO_OF_ALTERNATIVES];
                for(int j = 0; j < recitalTokens.length; j++) {                
                	int distanceBetweenIndices = -1, currentDistance = -1, indexOfShortestDistance = -1;
                	double currentDistanceWithMod = -1;


                    System.out.println(recitalTokens[j]);
                	System.out.println("\nsyro originalDefinitionTokens.length: " + originalDefinitionTokens.length
                    		+ " recitalTokens.length: " + recitalTokens.length);

                	System.out.println("\nsyro currentDistance, recitalTokens[j], originalDefinitionTokens[i]: ");
                	
                	Map<Double, String> sortedDistancesWithTokens = new TreeMap<Double, String>();
                	for(int i = 0; i < originalDefinitionTokens.length; i++) {
                    	currentDistance = 
                    			StringUtils.getLevenshteinDistance(recitalTokens[j], originalDefinitionTokens[i]);
                    	System.out.print(currentDistance + " " + recitalTokens[j] + " " +
                    			originalDefinitionTokens[i] + " ");
                    	distanceBetweenIndices = Math.abs(j - i);
                    	Log.i("DISTBETWEENINDICES: ", distanceBetweenIndices + "");
                    	String temp = "";
                    	if(distanceBetweenIndices < 10)
                    		temp = currentDistance + ".0" + distanceBetweenIndices;
                    	else
                    		temp = currentDistance + "." + distanceBetweenIndices;
                    	Log.i("currentDistanceWithMod", temp);
                    	currentDistanceWithMod = Double.parseDouble(temp);
                    	
            			// if the exact same double key hasn't already been added
            			if(!sortedDistancesWithTokens.containsKey(currentDistanceWithMod)) 
            				sortedDistancesWithTokens.put(currentDistanceWithMod, originalDefinitionTokens[i]);
                    }    
                	
                	int k = 0;
                	Iterator<Map.Entry<Double, String>> it = sortedDistancesWithTokens.entrySet().iterator();
                	// allowing only up to 5 alternatives per word
                	while(k < Constants.MAX_NO_OF_ALTERNATIVES && it.hasNext()) {
	                	  Map.Entry<Double, String> entry = it.next();
	              		  double key = entry.getKey();
	              		  double fractionalPart = key % 1;
	              		  System.out.println(key + " => " + entry.getValue());
	              		  if(k == 0 && (key >= 6 || fractionalPart >= 0.04)) {
	              			  // leave the token from the recital as it is
	              			  newTokensWithAlternatives[j][k++] = recitalTokens[j];
	              			  break;
	              		  }
	              		  else {
	                  		  newTokensWithAlternatives[j][k++] = entry.getValue();
	              		  }
                	}
                    // shortening length of newTokensWithAlternatives[j] to just the number of non-null elements

                    Log.i("TestActivity", "newTokensWithAlternatives before: " + Arrays.deepToString(newTokensWithAlternatives[j]));
                    newTokensWithAlternatives[j] = Arrays.copyOf(newTokensWithAlternatives[j], k);
                    Log.i("TestActivity", "newTokensWithAlternatives after: " + Arrays.deepToString(newTokensWithAlternatives[j]));

                    System.out.print("INDEX OUTSIDE FOR LOOP: " + indexOfShortestDistance);
                }
                
                amendedRecitalText = "";
                // printing the most likely recital text
                for(int i = 0; i < recitalTokens.length; i++) {
                	Log.i("newTokensWithAlternatives[i][0]: ", newTokensWithAlternatives[i][0]);
                	amendedRecitalText = amendedRecitalText + newTokensWithAlternatives[i][0] + " ";
                }
                
                // speechResult.setText(amendedRecitalText);
                //applySpannableToAll();
                
                allRecitalsWithAlternatives.add(newTokensWithAlternatives);

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
                
                wordArrayIndex++;
                Log.i("TA wordArrayIndex:", wordArrayIndex + "");
                Log.i("TA wordArrayLength:", wordArrayLength + "");
                if(wordArrayIndex == wordArrayLength) {
                	Log.i("TA: ", "inside if statement after WAI increment");
                	// the user has completed the test
                	startResultsActivity();
                	finish();
                }
                else {
                    recreate();
                }
                
            }
            break;
            }

        }
    }
    
    public void startResultsActivity() {
        try {
    		FileOutputStream fos = openFileOutput("wordArray", Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(WordViewer.wordArray);
            out.flush();
            out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Intent intent = new Intent(this, ResultsActivity.class);
    	startActivity(intent);
    }
    
}
