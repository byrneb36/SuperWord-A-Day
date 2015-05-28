package com.dcu.superword_a_day;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnalyzeResultsActivity extends Activity {
	private final int LIMIT_OF_INCORRECT_MATCHES = 4;
	private String [] semanticSimilarityResults;
	private boolean [] userDefinedResults;
	private String [] wordArray;
    private TextView semResults2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze_results);
		// ArrayList<String [][]> ResultsActivity.allRecitalsWithAlternatives
		// HashMap<String, String> WordViewer.wordAndDefinitions
		// String [] WordViewer.wordArray
		
		int numberOfWords = -1;
		boolean testFlag = false;
		String [] testRecitalsArray, testDefinitionsArray;

		wordArray = new String[0];
		testRecitalsArray = new String[0];
		testDefinitionsArray = new String[0];

        Intent startingIntent = getIntent();
        String source = startingIntent.getStringExtra("source");
		Log.i("ARDA", "source: " + source);
		if(source.equals(Constants.SOURCE_ANALYZE_RESULTS_DRIVER_ACTIVITY)) {
			Log.i("ARDA", "inside source ARDA");
			testFlag = true; // indicates when to use testRecitals and testDefinitions arrays
			// get arrays for words, recital strings and definition strings from intent
			wordArray = startingIntent.getStringArrayExtra("testWordArray");
			testRecitalsArray = startingIntent.getStringArrayExtra("testRecitalsArray");
			testDefinitionsArray = startingIntent.getStringArrayExtra("testDefinitionsArray");
			// use the length of word array instead of wordArray.length
			numberOfWords = wordArray.length;
			
		}
		else if(source.equals(Constants.SOURCE_RESULTS_ACTIVITY)) {
			Log.i("ARDA", "inside source ResultsActivity");
			numberOfWords = WordViewer.wordArray.length;
			wordArray = WordViewer.wordArray;
		}
		
		// word-by-word comparison
		String definitionStr, recitalStr = "";
		String [][] temp;
		String [] definitionTokens, recitalTokens;
		boolean [] word_by_wordA_results = new boolean[numberOfWords];
		boolean [] word_by_wordB_results = new boolean[numberOfWords];
		boolean [] keyword_by_keywordA_results = new boolean[numberOfWords];
		boolean [] keyword_by_keywordB_results = new boolean[numberOfWords];
		boolean [][] all_results_arrays = {word_by_wordA_results, word_by_wordB_results, keyword_by_keywordA_results,
				keyword_by_keywordB_results};
		String [] definitionStrings = new String[numberOfWords];
		String [] recitalStrings = new String[numberOfWords];
		semanticSimilarityResults = new String[numberOfWords];
		userDefinedResults = new boolean[numberOfWords];

        semResults2 = new TextView(this);

		for(int i = 0; i < numberOfWords; i++) {
			if(testFlag) {
				definitionStr = testDefinitionsArray[i];
				recitalStr = testRecitalsArray[i];
			}
			else {
				definitionStr = WordViewer.wordAndDefinitions.get(WordViewer.wordArray[i]);
				temp = ResultsActivity.allRecitalsWithAlternatives.get(i);
				
	            for(int j = 0; j < temp.length; j++) {
	            	Log.i("ARA temp[i][0]: ", temp[j][0]);
	            	recitalStr = recitalStr + temp[j][0] + " ";
	            }
			}
            definitionTokens = definitionStr.split("\\W");
            recitalTokens = recitalStr.split("\\W");

            // creating URL for semantic similarity test
            String url = "http://swoogle.umbc.edu/StsService/GetStsSim?operation=api&phrase1=";
            for(int j = 0; j < definitionTokens.length; j++) {
            	url = url + definitionTokens[j] + "%20";
            }
            // completing URL
            url = url.substring(0, url.length()-3); // the last "%20" is unnecessary
            url = url + "&phrase2=";
            for(int j = 0; j < recitalTokens.length; j++) {
            	url = url + recitalTokens[j] + "%20";
            }
            url = url.substring(0, url.length()-3);
            Log.i("ARA URL", url);
        	
            
            Log.i("definitionTokens", Arrays.toString(definitionTokens));
            Log.i("recitalTokens", Arrays.toString(recitalTokens));

    		Boolean word_by_wordA = true, word_by_wordB = null;
    		
    		// doing the word_by_wordA test first
            // if their lengths don't match, they can't be exact matches
            if(recitalTokens.length != definitionTokens.length) {
                Log.i("word by word A NOT AN EXACT LENGTH MATCH", "definitionTokens length: " + definitionTokens.length +
                        " recitalTokens length: " + recitalTokens.length);
                word_by_wordA = false;
            }
            else {
                for(int j = 0; (j < definitionTokens.length); j++) {
                    if(!definitionTokens[j].equals(recitalTokens[j])) {
                        word_by_wordA = false;
                        break;
                    }
                }
            }

    		
    		// the word_by_wordB test
    		int definitionTokensIndex = 0, recitalTokensIndex = 0;
            while(definitionTokensIndex < definitionTokens.length && recitalTokensIndex < recitalTokens.length) {
            	if(!definitionTokens[definitionTokensIndex].equals(recitalTokens[recitalTokensIndex])) {
            		int recitalTokensIndexExtension = 0;
        			while(recitalTokensIndexExtension < LIMIT_OF_INCORRECT_MATCHES && 
        					recitalTokensIndex + recitalTokensIndexExtension < recitalTokens.length) {
            			if(definitionTokens[definitionTokensIndex].equals(recitalTokens[recitalTokensIndex + 
            			                                            recitalTokensIndexExtension])) {
                    		recitalTokensIndex = recitalTokensIndex + recitalTokensIndexExtension;
                    		break;
                    	}
            			else {
            				recitalTokensIndexExtension++;
            				// if the recitalTokensIndex extension has been depleted without a match, try looking
            				// ahead on definitionTokensIndex
            				if(recitalTokensIndexExtension == LIMIT_OF_INCORRECT_MATCHES) {
            					int definitionTokensIndexExtension = 0;
            					while(definitionTokensIndexExtension < LIMIT_OF_INCORRECT_MATCHES && 
            							definitionTokensIndex + definitionTokensIndexExtension < definitionTokens.length) {
            						if(definitionTokens[definitionTokensIndex + definitionTokensIndexExtension]
            								.equals(recitalTokens[recitalTokensIndex])) {
            							definitionTokensIndex = definitionTokensIndex + definitionTokensIndexExtension;
            						}
            						definitionTokensIndexExtension++;
            					}
            				}
            				else {
            					// there are no matches on either side, so the recital is deemed incorrect
            					word_by_wordB = false;
            				}
            			}
            		}
           
            	}
            	if(word_by_wordB != null && word_by_wordB == false) {
            		// there is no need to continue
            		break;
            	}
            	else {
            		definitionTokensIndex++;
            		recitalTokensIndex++;
            	}
            }
            word_by_wordA_results[i] = word_by_wordA;
            if(word_by_wordB == null) {
            	word_by_wordB_results[i] = true;
            }
            else {
            	word_by_wordB_results[i] = false;
            }
            
            // keyword_by_keyword test A
            ArrayList<String> stopwordsList = new ArrayList<String>();
            try {
            	InputStream in = this.getAssets().open("Stopwords.txt");
				BufferedReader buf = new BufferedReader(new InputStreamReader(in));
				String nextLine = buf.readLine();
				while(nextLine != null) {
					stopwordsList.add(nextLine);
					nextLine = buf.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            ArrayList<String> newDefinitionTokens = new ArrayList<String>();
            ArrayList<String> newRecitalTokens = new ArrayList<String>();
            
            for(int a = 0; a < definitionTokens.length; a++) {
            	if(!stopwordsList.contains(definitionTokens[a])) {
            		newDefinitionTokens.add(definitionTokens[a]);
            	}
            }
            for(int b = 0; b < recitalTokens.length; b++) {
            	if(!stopwordsList.contains(recitalTokens[b])) {
            		newRecitalTokens.add(recitalTokens[b]);
            	}
            }
            boolean keyword_by_keywordA = true;
            
            ListIterator<String> def = newDefinitionTokens.listIterator();
            ListIterator<String> rec = newRecitalTokens.listIterator();
            while(def.hasNext()) {
            	String nextToken = (String) def.next();
            	
            	if(!rec.hasNext()) {
            		keyword_by_keywordA = false;
            		break;
            	}
            	else if(!nextToken.equals(rec.next())) {
            		keyword_by_keywordA = false;
            		break;
            	}
            }
            
            // keyword_by_keyword test B
            boolean keyword_by_keywordB = true;
            while(def.hasPrevious()) {
            	if(!newRecitalTokens.contains(def.previous())) {
            		keyword_by_keywordB = false;
            		break;
            	}
            }
            
            // storing results
            keyword_by_keywordA_results[i] = keyword_by_keywordA;
            keyword_by_keywordB_results[i] = keyword_by_keywordB;
            
            // semantic similarity test
            Bundle mBundle = new Bundle();
            mBundle.putInt("for loop index", i);
            mBundle.putString("url", url);
            new SemanticSimilarityService().execute(mBundle);
            
            // saving definition and recital strings to display for the user to do their own comparison
            definitionStrings[i] = definitionStr;
            recitalStrings[i] = recitalStr;
		}
		
		 // displaying the results to the user
		 final ListView resultsList = (ListView) findViewById(R.id.resultsList);
		 String [] results_text = {"Word-by-word comparison: ", "Word-by-word comparison " +
		 		"(allowing up to 3 missing or superfluous words per matching word): ", "Keyword-by-keyword comparison: ",
		 		 "Keyword-by-keyword comparison (ignoring order): "};
		 for(int j = 0; j < all_results_arrays.length; j++) {
			 final TextView resultsView = new TextView(this);
			 int count = 0;
			 for(int i = 0; i < numberOfWords; i++) {
				 if(all_results_arrays[j][i] == true)
					 count++;
			 }			 
			 resultsView.setText(results_text[j] + count + "/" + numberOfWords);
			 resultsList.addFooterView(resultsView);
		 }
         final TextView semResults1 = new TextView(this);
         //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
         //       RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
         //params.addRule(RelativeLayout.RIGHT_OF, semResults2.getId());
         //final ProgressBar p = new ProgressBar(this, null,  android.R.attr.progressBarStyleSmall);
         // semResults1.setVisibility(View.GONE);
		 semResults1.setText("Comparison using the UMBC semantic similarity service: ");
         /*
		 for(int i = 0; i < numberOfWords; i++) {
			 semResults1.append(semanticSimilarityResults[i] + " ");
		 }
		 */
		 resultsList.addFooterView(semResults1);
         resultsList.addFooterView(semResults2);

		 final TextView defAndRecHeading = new TextView(this);
		 defAndRecHeading.setText("Each word followed by its definition and recital, allowing the user to assess their" +
		 		" own performance:");
		 defAndRecHeading.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Large);
		 resultsList.addFooterView(defAndRecHeading);
		 for(int j = 0; j < numberOfWords; j++) {
			 final TextView wordString = new TextView(this);
			 wordString.setText(wordArray[j]);
			 wordString.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Large);
			 final TextView defAndRecStrings = new TextView(this);
			 defAndRecStrings.setText("Definition " + (j + 1) + ": " + definitionStrings[j] + "\n" +
			 "Recital " + (j + 1) + ": " + recitalStrings[j]);
			 resultsList.addFooterView(wordString);
			 resultsList.addFooterView(defAndRecStrings);
			 resultsList.addFooterView(addPassFailRadioGroup(j));
		 }

        // adding a submit button that returns the user to the main menu screen and uploads test results to the database
        Button submit = new Button(this);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // contains each word followed by "passed" or "failed"
                ArrayList<String> uploadData = new ArrayList<String>();

                // saving the words the user has failed so that they can be highlighted in WordArchiveActivity
                SharedPreferences failedWordsStore = getSharedPreferences(Constants.FAILED_WORDS_FILE, MODE_PRIVATE);
                HashSet<String> failedWords = new HashSet<String>();
                for(int i = 0; i < userDefinedResults.length; i++) {
                    if(!userDefinedResults[i]) {
                        uploadData.add(wordArray[i]);
                        uploadData.add("failed");
                        failedWords.add(wordArray[i]);
                    }
                }
                Log.i("ARA", "Failed words: " + failedWords.toString());
                SharedPreferences.Editor editor = failedWordsStore.edit();
                editor.putStringSet("failedWords", failedWords);
                editor.commit();

                // adding all the words the user has marked as passed or has left unchanged (which are marked passed by default)
                for(int i = 0; i < wordArray.length; i++) {
                    if(!uploadData.contains(wordArray[i])) {
                        uploadData.add(wordArray[i]);
                        uploadData.add("passed");
                    }
                }

                // using an intent service to upload test results to the database
                Intent uploadService = new Intent(view.getContext(), UploadIntentService.class);
                uploadService.putStringArrayListExtra("resultsData", uploadData);
                startService(uploadService);

                // returning to the main  menu
                Intent intent = new Intent(view.getContext(), MainMenuActivity.class);
                startActivity(intent);
            }
        });
        resultsList.addFooterView(submit);

		 ArrayAdapter mAdapter = new ArrayAdapter(this, R.layout.activity_analyze_results_row);
		 resultsList.setAdapter(mAdapter);
		 Log.i("ARA", "User-defined results: " + Arrays.toString(userDefinedResults));
	}
	
	private RadioGroup addPassFailRadioGroup(final int resultIndex) {
		 RadioGroup passFailGroup = new RadioGroup(this);
		 passFailGroup.setId(resultIndex);
		 RadioButton passButton = new RadioButton(this);
		 passButton.setText("Pass");
		 passButton.setId(0);
		 RadioButton failButton = new RadioButton(this);
		 failButton.setText("Fail");
		 failButton.setId(-1);
		 passFailGroup.addView(passButton);
		 passFailGroup.addView(failButton);
		 passFailGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
         {
	          public void onCheckedChanged(RadioGroup group, int checkedId) {
	      		  Log.i("ARA", "Inside onCheckedChanged");
	              if(checkedId == 0)
	            	  userDefinedResults[group.getId()] = true;
	              else if(checkedId == -1)
	            	  userDefinedResults[group.getId()] = false;
	          }
              
         });
		 return passFailGroup;
	}
	
	
	private class SemanticSimilarityService extends AsyncTask<Bundle, Void, Void> {
        private String response = "";
        private int index;

        @Override
        protected Void doInBackground(Bundle... params) {
        	Bundle[] mBundles = params;
        	index = mBundles[0].getInt("for loop index");
            String url = mBundles[0].getString("url");
            Log.i("asyncTask url", "url: " + url);
            HttpUriRequest httpRequest = new HttpGet(url);
            HttpResponse httpResponse;
			try {
				httpResponse = new DefaultHttpClient().execute(httpRequest);
				response = new BasicResponseHandler().handleResponse(httpResponse);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            Log.i("httpResponse", response);
	   		return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            semResults2.append("" + (index + 1)  + ": " + response);
        }
	}

}













