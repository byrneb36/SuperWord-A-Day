package com.dcu.superword_a_day;

import org.apache.commons.io.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Calendar;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TodaysWords extends Activity {
	
	private String stringURL = "https://www.wordnik.com/word-of-the-day/";
	private ProgressBar spinner;
	private int sharedPrefNoOfWords;
	private String archivePosition;
	private GregorianCalendar startDate = new GregorianCalendar(2009, 9, 31);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todays_words);
	    spinner = (ProgressBar)findViewById(R.id.progressBar1);
	    spinner.setVisibility(View.GONE);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		
	    SharedPreferences settings = getSharedPreferences(Constants.OPTIONS_FILE, MODE_PRIVATE);
	    // if no preference is specified, default to 5
	    sharedPrefNoOfWords = settings.getInt("numOfWordsPerDay", 5);
	    // getting archive position indicator
	    archivePosition = settings.getString("WordnikIndicator", "n/a");

    	WordArchive myWordArchive = new WordArchive(getApplicationContext());
    	
    	// have Today's Words not been scraped yet?
    	if(!myWordArchive.checkForTodaysWords(sharedPrefNoOfWords)) {
    		// has the archive position been saved before?
    	    if(!archivePosition.equals("n/a")) {
    			Date d = new Date();
    			try {
    				d = dateFormat.parse(archivePosition);
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}
    			startDate.setTime(d);	
    			
    			// advancing the archive position indicator for when it's next needed
    			GregorianCalendar saveDate = new GregorianCalendar();
    			saveDate.setTime(d);
    			saveDate.add(Calendar.DAY_OF_MONTH, +sharedPrefNoOfWords);
    			SharedPreferences.Editor editor = settings.edit();
    			editor.putString("WordnikIndicator", dateFormat.format(saveDate.getTime()));
    			editor.commit();
    	    }
    	    else {
    	    	GregorianCalendar saveDate = (GregorianCalendar) startDate.clone();
    			SharedPreferences.Editor editor = settings.edit();
    			saveDate.add(Calendar.DAY_OF_MONTH, +sharedPrefNoOfWords);
    			editor.putString("WordnikIndicator", dateFormat.format(saveDate.getTime()));
    			editor.commit();
    	    }
			
		    spinner.setVisibility(View.VISIBLE);
		    
	        new CaptureWord().execute();       
    	}
    	else {
    		startWordViewer();
    	}
	}
	
	private void startWordViewer() {
    	Intent intent = new Intent(this, WordViewer.class);
    	intent.putExtra("source", Constants.SOURCE_TODAYS_WORDS);
    	startActivity(intent);
	}

	private boolean checkForDuplicates(String word) {
	    SharedPreferences mWordArchive = getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, MODE_PRIVATE);
	    boolean result = mWordArchive.contains(word);
	    Log.i("TodaysWords", "checkForDuplicates word: " + word + " returns: " + result);
	    return result;
	}
	
	private void addToWordData(LinkedList<LinkedList<String>> w) throws IOException {
		FileOutputStream fos = openFileOutput(Constants.WORD_DATA_FILE, Context.MODE_PRIVATE);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(w);
        out.flush();
        out.close();
	}
	
	private LinkedList<LinkedList<String>> deepCopy(LinkedList<LinkedList<String>> orig) {
		LinkedList<LinkedList<String>> myLinkedList = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            myLinkedList = (LinkedList<LinkedList<String>>)in.readObject();
            in.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return myLinkedList;
	}


    private void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

	private class CaptureWord extends AsyncTask<Void, Void, Void> {
		LinkedList<LinkedList<String>> tempWordData = new LinkedList<LinkedList<String>>();
        LinkedList<String> scrapedWords = new LinkedList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
  			 Log.d("ASYNC", "bl2");

        }

        @Override
        protected Void doInBackground(Void... params) {
   		 try {
             // Connect to the web site
   			 Document [] documents = new Document[sharedPrefNoOfWords];
   			 Elements word, source, senses;
   			 LinkedList<String> newWordData;
   			 ListIterator<Element> sense;
   			 
   			String fullDate = "";
 			String newURL = "";
 			// create as many urls as the number of words the user has chosen to learn per day
   			 for(int i = 0; i < sharedPrefNoOfWords; i++) {
  				startDate.add(Calendar.DAY_OF_MONTH, +1);
   				 // creating new URL
   				fullDate = startDate.get(Calendar.YEAR) + "/" + 
 						(startDate.get(Calendar.MONTH)+1) + "/" + 
 						startDate.get(Calendar.DAY_OF_MONTH);
 				newURL = stringURL + fullDate;
 				 System.out.println("syro newURL: " + newURL);


                 try {
                     enableSSLSocket();
                 } catch (KeyManagementException e) {
                     e.printStackTrace();
                 } catch (NoSuchAlgorithmException e) {
                     e.printStackTrace();
                 }

                 URLConnection connection = new URL(newURL).openConnection();
                 InputStream inStream = connection.getInputStream();
                 String htmlText = org.apache.commons.io.IOUtils.toString(inStream, connection.getContentEncoding());

                 documents[i] = Jsoup.parse(htmlText);

                 // Using Elements to get the Meta data
  	             word = documents[i].select("h1 > a");
  	             source = documents[i].select("h3[class=source]");
  	             senses = documents[i].select("div[class=guts active] li");
   	  		  	 
   	             System.out.println("syro -1: " + word.text());
   	             if(scrapedWords.contains(word.text()) || checkForDuplicates(word.text())) {
   	            	 Log.i("TAG", "REPEATING ITERATION");
   	            	 // repeat for loop iteration
   	            	 i--;
   	             }
   	             // if there are no duplicates, add the new word & definition
   	             else {
   	            	 Log.i("TodaysWords", "WORD: " + word.text() + " added.");
   	            	 scrapedWords.add(word.text());
	   	   			 newWordData = new LinkedList<String>();
	   	             newWordData.add(word.text());
	   	             newWordData.add(source.text());
	   	             
	   	             sense = senses.listIterator();
	   	             
	   	             while(sense.hasNext()) {
	   	            	 newWordData.add(sense.next().text());
	   	             }
	   	             tempWordData.add(newWordData);
   	             }
   			 }          
         } catch (IOException e) {
             e.printStackTrace();
         }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
        	WordArchive myWordArchive = new WordArchive(getApplicationContext());
        	try {
				addToWordData(tempWordData);
			} catch (IOException e) {
				Log.i("TodaysWords", "ADD TO WORD DATA FAILED");
				e.printStackTrace();
			}
        	LinkedList<LinkedList<String>> tempWordData2 = deepCopy(tempWordData);
        	myWordArchive.saveToArchive(tempWordData2);
        	Log.i("TodaysWords onPostExecute",tempWordData2.toString());

            // starting DownloadIntentService here
            Intent downloadService = new Intent(getApplicationContext(), DownloadIntentService.class);
            String[] wordsArr = scrapedWords.toArray(new String[scrapedWords.size()]);
            // adding the words whose difficulty percentages are to be retrieved from the server
            downloadService.putExtra("wordsArr", wordsArr);
            startService(downloadService);

        	startWordViewer();
    	    spinner.setVisibility(View.GONE);
        }
    }
}

