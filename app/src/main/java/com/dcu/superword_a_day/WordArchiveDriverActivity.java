package com.dcu.superword_a_day;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class WordArchiveDriverActivity extends Activity {

	private String url = "https://www.wordnik.com/word-of-the-day/";	
	private ProgressBar spinner;
	private int noOfWords, noOfDays;
	private GregorianCalendar startDate = new GregorianCalendar(2009, 9, 31);
	private Calendar cal = GregorianCalendar.getInstance();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
	private int noOfDaysIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_archive_driver);
	}
	
	// prints directories and clears all internal storage and shared preferences files
	public void clearFiles(View view) {
		File dir = getFilesDir();
		String [] fileNames, fileNames2;
		fileNames = dir.list();
        for(int i = 0; i < fileNames.length; i++) {
			File file = new File(dir, fileNames[i]);
			try {
				new RandomAccessFile(file, "rw").setLength(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.i("WADA", "File file length: " + file.length());
        }
		Log.i("WADA Internal Storage", "Internal Storage Files: " + Arrays.toString(fileNames));
	    File prefsdir = new File(getApplicationInfo().dataDir,"shared_prefs");
	    if(prefsdir.exists() && prefsdir.isDirectory()){
	    	fileNames2 = prefsdir.list();
	        Log.i("WADA SharedPreferences", "SharedPreferences Files: " + Arrays.toString(fileNames2));	    
	        for(int i = 0; i < fileNames2.length; i++) {
		    	File f = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + fileNames2[i]);
				try {
					new RandomAccessFile(f, "rw").setLength(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.i("WADA", "File f length: " + f.length());
		    }
	    }
	    Log.i("WADA clearFiles", "Internal storage directory: files cleared. SharedPreferences directory: " +
	    		"files cleard.");
	}
	
	// scrape and save a month of words to WordArchive
	public void populateWordArchive(View view) {
	    spinner = (ProgressBar)findViewById(R.id.progressBar1);
	    spinner.setVisibility(View.VISIBLE);
	    
	    // scrape 3 words per day for a period of 28 days
	    noOfWords = 3; noOfDays = 28;
		cal.add(Calendar.DAY_OF_MONTH, -noOfDays);
    	Log.i("WADA", "cal date: " + dateFormat.format(cal.getTime()));
		new WADACaptureWord().execute();
	    
	}
	
	private boolean checkForDuplicates(String word) {
		File f = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.DEFINITION_ARCHIVE_FILE 
				+ ".xml");
		if(f.length() > 0) {
			Log.i("WADA", "checkForDuplicates: Archive file contains content");
		    SharedPreferences mWordArchive = getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, MODE_PRIVATE);
		    if(mWordArchive.contains(word)) {
		    	Log.i("WADA", "checkForDuplicates: Archive contains word");
		    	return true;
		    }
		    else return false;
		}
		else return false;
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
	
	private class WADACaptureWord extends AsyncTask<Void, Void, Void> {
		LinkedList<LinkedList<String>> tempWordData = new LinkedList<LinkedList<String>>();
		String wordArchiveSaveDate = dateFormat.format(cal.getTime());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
  			 Log.d("ASYNC", "bl2");

        }

        @Override
        protected Void doInBackground(Void... params) {
   		 try {
             // Connect to the web site
   			 Document [] documents = new Document[noOfWords];
   			 Elements word, source, senses;
   			 LinkedList<String> newWordData;
   			 ListIterator<Element> sense;
   			 
   			String fullDate = "";
 			String newURL = "";
 			LinkedList<String> scrapedWords = new LinkedList<String>(); 
 			// create as many urls as the number of words the user has chosen to learn per day
   			 for(int i = 0; i < noOfWords; i++) {
  				startDate.add(Calendar.DAY_OF_MONTH, +1);
   				 // creating new URL
   				fullDate = startDate.get(Calendar.YEAR) + "/" + 
 						(startDate.get(Calendar.MONTH)+1) + "/" + 
 						startDate.get(Calendar.DAY_OF_MONTH);
 				newURL = url + fullDate;
 				 System.out.println("syro newURL: " + newURL);
 				documents[i] = Jsoup.connect(newURL).get();
 				// Using Elements to get the Meta data
  	             word = documents[i].select("h1 > a");
  	             source = documents[i].select("h3[class=source]");
  	             senses = documents[i].select("div[class=guts active] li");
   	  		  	 
   	             System.out.println("syro -1: " + word.text());

   	             if(!(scrapedWords.contains(word.text()) || checkForDuplicates(word.text()))) {
	            	 System.out.println("syro WORD: " + word.text() + " added.");
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
   	             else {
   	            	 Log.i("TAG", "REPEATING ITERATION");
   	            	 // repeat for loop iteration
   	            	 i--;
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
				e.printStackTrace();
			}
        	LinkedList<LinkedList<String>> tempWordData2 = deepCopy(tempWordData);
        	myWordArchive.saveToArchive(tempWordData2, wordArchiveSaveDate);
        	Log.i("WADA onPostExecute", tempWordData2.toString());
        	if(noOfDaysIndex < 27) {
        		noOfDaysIndex++;
        		cal.add(Calendar.DAY_OF_MONTH, +1);
            	Log.i("WADA", "cal date: " + dateFormat.format(cal.getTime()));
        		new WADACaptureWord().execute();
        	}
        	else {
        		// Advancing the Wordnik indicator
        	    SharedPreferences settings = getSharedPreferences(Constants.OPTIONS_FILE, MODE_PRIVATE);
    			SharedPreferences.Editor editor = settings.edit();
    			editor.putString("WordnikIndicator", dateFormat.format(startDate.getTime()));
    			editor.commit();
    			
        	    spinner.setVisibility(View.GONE);
        	}
        }
    }
}
