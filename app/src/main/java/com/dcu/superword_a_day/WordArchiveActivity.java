package com.dcu.superword_a_day;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WordArchiveActivity extends ListActivity {
  	private ArrayList<Integer> wordIndexes = new ArrayList<Integer>();
  	private ArrayList<Integer> failedWordsIndexes = new ArrayList<Integer>();
  	private HashSet<String> failedWords = new HashSet<String>();
  	private Calendar cal = GregorianCalendar.getInstance();

  	@Override
  	protected void onListItemClick(ListView l, View v, int position, long id) {
  		//LinkedList<LinkedList<String>> wordsAndDefinitions = new LinkedList<LinkedList<String>>();
        LinkedList<TreeMap> fullWordDataList = new LinkedList<TreeMap>();
  		// finding the date that the selected word falls under
  		int itemViewPosition = position;
  		String dateOfSelectedWord;
  		int relativeItemPosition = 0; // needed for setting the current pager item
  		int itemViewType = l.getAdapter().getItemViewType(itemViewPosition);
  		if(itemViewType != 1) {
  			itemViewPosition--; 
  			itemViewType = l.getAdapter().getItemViewType(itemViewPosition);
  			while(itemViewType != 1) {
  	  			itemViewPosition--; 
	  			relativeItemPosition++;
	  			itemViewType = l.getAdapter().getItemViewType(itemViewPosition);
  			} 
  		}
  		dateOfSelectedWord = l.getItemAtPosition(itemViewPosition).toString();
  		Log.i("WAA ListItemClick", "List Item Click: " + l.getItemAtPosition(position).toString() + " Date of selected: "
  				+ dateOfSelectedWord);
  		// loading all word data for this date from the archive
  		WordArchive mWordArchive = new WordArchive(this);
  		fullWordDataList = mWordArchive.loadFromArchive(dateOfSelectedWord);
  		// saving to internal storage
		Log.i("WAA", "fullWordDataList.toString: " + fullWordDataList.toString());
		Log.i("WAA Files Dir Path", "Files Dir Path: " + this.getFilesDir().getPath());
		FileOutputStream fos;
		try {
			File file = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.WORD_ARCHIVE_DATA_FILE
					+ ".xml");
			if(file.length() > 0) {
				try {
					new RandomAccessFile(file, "rw").setLength(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.i("WAA clear", "file cleared");
			}
			fos = this.openFileOutput(Constants.WORD_ARCHIVE_DATA_FILE, 0);
	        ObjectOutputStream out = new ObjectOutputStream(fos);
	        out.writeObject(fullWordDataList);
	        out.flush();
	        out.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Intent intent = new Intent(this, WordViewer.class);
    	intent.putExtra("source", Constants.SOURCE_WORD_ARCHIVE_ACTIVITY);
    	intent.putExtra("numOfWords", fullWordDataList.size());
    	intent.putExtra("itemPosition", relativeItemPosition);
    	startActivity(intent);
  	}
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Retrieving the set of failed words from internal storage
		SharedPreferences failedWordsArchive = getSharedPreferences(Constants.FAILED_WORDS_FILE, 0);
		failedWords = (HashSet<String>) failedWordsArchive.getStringSet("failedWords", new HashSet<String>());
		Log.i("WAA", "WAA failedWords: " + failedWords.toString());
		
		GregorianCalendar mDateOfCreation = new GregorianCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		SharedPreferences datesArchive = getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		System.out.println("syro WordArchiveActivity DATESARCHIVE: " + datesArchive.toString());
		Map<String, ?> allEntries = datesArchive.getAll();   
		System.out.println("syro WordArchiveActivity ALLENTRIES: " + allEntries.toString());
	    final ArrayList<String> dateListChronological = new ArrayList<String>();
		String dateOfCreation = (String) allEntries.get("dateOfCreation");
		System.out.println("syro WordArchiveActivity DATEOFCREATION: " + dateOfCreation);
		Date d = new Date();
		try {
			d = dateFormat.parse(dateOfCreation);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mDateOfCreation.setTime(d);
		System.out.println("syro mDateOfCreation: " + dateFormat.format(mDateOfCreation.getTime()));
		// cycle through all values in the dates archive starting from dateOfCreation 
		// subtracting 1 from the size due to the inclusion of dateOfCreation in the dates archive file
		int allEntriesIndex = 0; String date = "";
		// if all entries have been found or all possible dates have been checked, don't continue
		do {
			date = dateFormat.format(mDateOfCreation.getTime());
			System.out.println("syro WAA date: " + date);
			if(allEntries.containsKey(date)) {
				dateListChronological.add(date);
				allEntriesIndex++;
			}
			mDateOfCreation.add(Calendar.DAY_OF_MONTH, +1);
		} while(allEntriesIndex < allEntries.size()-1 && !date.equals(dateFormat.format(cal.getTime())));
		
		System.out.println("syro WAA DLS.size(): " + dateListChronological.size() + " DLS.toString(): "
				+ dateListChronological.toString());
		MyArrayAdapter adapter = new MyArrayAdapter(this, dateListChronological);
		setListAdapter(adapter);
	  	int k = 0;	Iterator wordsIterator;
	  	String nextItem = "";
	  	int dateListOriginalSize = dateListChronological.size();
	  	System.out.println("syro WAA dateListOriginalSize set: " + dateListOriginalSize);
		for(int i = 0; i < dateListOriginalSize; i++) {
		  	// insert all words associated with the date
		  	Set<String> words = (HashSet<String>)allEntries.get(dateListChronological.get(k + i));
		  	wordsIterator = words.iterator();
		    while(wordsIterator.hasNext()) {
		    	nextItem = (String) wordsIterator.next();
				adapter.insert(nextItem, k + i + 1);
				if(failedWords.contains(nextItem))
					failedWordsIndexes.add(k+i+1);
				else 
					wordIndexes.add(k+i+1);
				k++;
		    }
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.word_archive, menu);
		return true;
	}
	private class MyArrayAdapter extends ArrayAdapter<String> {
		  private final Context context;
		  private final List<String> values;
		  
		  public MyArrayAdapter(Context context, List<String> values) {
			    super(context, R.layout.archive_row_layout, values);
			    this.context = context;
			    this.values = values;
		  }
		  
		  @Override
		  public int getViewTypeCount() {
			  	return 3;
		  }
		  
		  @Override
		  public int getItemViewType(int position) {
			  if(wordIndexes.contains(position))
				  return 0;
			  else if(failedWordsIndexes.contains(position))
				  return 2;
			  else return 1;
		  }
		  
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			int viewType = getItemViewType(position);
			if(rowView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				/*
				if(viewType == 1) {
					System.out.println("syro WAA inflating layout A");
					rowView = inflater.inflate(R.layout.archive_row_layout, parent, false);
				}
				else {
					rowView = inflater.inflate(R.layout.archive_row_layout_b, parent, false);
				}
				*/
				switch(viewType) {
					case 0: rowView = inflater.inflate(R.layout.archive_row_layout_b, parent, false);
						break;
					case 1: Log.i("WAA", "Inflating date layout");
						rowView = inflater.inflate(R.layout.archive_row_layout, parent, false);
						break;
					case 2: Log.i("WAA", "Inflating highlighted layout");
						rowView = inflater.inflate(R.layout.archive_row_layout_c, parent, false);
						break;
					default: Log.i("WAA", "VIEWTYPE NOT RECOGNIZED");
						break;
				}
			}
		    TextView textView = (TextView) rowView.findViewById(R.id.textView1);
		    textView.setText(values.get(position));
		    return rowView;
		  }
		  
	}	
}
