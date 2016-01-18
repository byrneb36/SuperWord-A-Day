package com.dcu.superword_a_day;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class TodaysRevision {
	private Context c;
    private int firstRevisionInterval, secondRevisionInterval, thirdRevisionInterval, fourthRevisionInterval;
    private LinkedList<TreeMap> fullWordDataList;
    private LinkedList<TreeMap> tempFullWordDataList;
    
	public TodaysRevision (Context c) {
		this.c = c;
	}
    
    public void loadRevisionIntervals() {
	    SharedPreferences settings = c.getSharedPreferences(Constants.OPTIONS_FILE, 0);
	    firstRevisionInterval = settings.getInt("firstRevisionInterval", -1);
	    secondRevisionInterval = settings.getInt("secondRevisionInterval", -1);
	    thirdRevisionInterval = settings.getInt("thirdRevisionInterval", -1);
	    fourthRevisionInterval = settings.getInt("fourthRevisionInterval", -1);
    }
    
    public boolean retrieveFromWordArchive() {
    	WordArchive mWordArchive = new WordArchive(c);
    	boolean revisionWordsAvailable = false;
    	int [] revisionIntervals = {firstRevisionInterval, secondRevisionInterval, 
    			thirdRevisionInterval, fourthRevisionInterval};
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    	Calendar cal = GregorianCalendar.getInstance();
    	Log.i("RFWA today's date", dateFormat.format(cal.getTime()));
    	fullWordDataList = new LinkedList<TreeMap>();
    	tempFullWordDataList = new LinkedList<TreeMap>();
    	for(int i = 0; i < revisionIntervals.length; i++) {
        	if(revisionIntervals[i] != 0) {
            	cal.add(Calendar.DAY_OF_MONTH, -revisionIntervals[i]);
            	Log.i("RFWA revision date number: " + i, dateFormat.format(cal.getTime()));
                tempFullWordDataList = mWordArchive.loadFromArchive(dateFormat.format(cal.getTime()));
    			Log.i("TodaysRevision", "tempFullWordDataList.toString: " + tempFullWordDataList.toString());
            	if(!tempFullWordDataList.isEmpty()) {
            		revisionWordsAvailable = true;
            		appendToWordsAndDefinitions(tempFullWordDataList);
            	}
            	else
            		Log.i("TodaysRevision", "revision interval " + i + " loads no words");
                // resetting 'cal' to the current date for the next iteration
                cal.add(Calendar.DAY_OF_MONTH, revisionIntervals[i]);
        	}    		
        	else
        		Log.i("TodaysRevision interval", "interval " + i + " is equal to zero");
    	}
		if(revisionWordsAvailable) {
			// save those words for use by WordViewer
			Log.i("TodaysRevision", "WordsAndDefinitions.toString: " + fullWordDataList.toString());
			FileOutputStream fos;
			try {
				File file = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.REVISION_DATA_FILE
						+ ".xml");
				if(file.exists()) {
					c.deleteFile(Constants.REVISION_DATA_FILE);
					Log.i("TodaysRevision delete", "file deleted");
				}
				fos = c.openFileOutput(Constants.REVISION_DATA_FILE, 0);
		        ObjectOutputStream out = new ObjectOutputStream(fos);
		        out.writeObject(fullWordDataList);
		        out.flush();
		        out.close();	
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		else {
			Log.i("TodaysRevision", "NO WORDS FOUND FOR REVISION");
			return false;
		}
    }
    
    public void startWordViewer() {
    	Intent intent = new Intent(c, WordViewer.class);
    	intent.putExtra("source", Constants.SOURCE_TODAYS_REVISION);
    	intent.putExtra("numOfWords", fullWordDataList.size());
    	c.startActivity(intent);
    }
    
    // appends to wordsAndDefinitions new 2D LinkedLists of words & definitions loaded from Word Archive
    private void appendToWordsAndDefinitions(LinkedList<TreeMap> toAppend) {
    	Iterator it = toAppend.iterator();
    	while(it.hasNext()) {
    		fullWordDataList.add((TreeMap) it.next());
    	}
    }
}
