package com.dcu.superword_a_day;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import com.google.gson.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class WordArchive {
	private Context c;
    
	public WordArchive (Context c) {
		this.c = c;
	}

    private String convertToJSON(TreeMap l) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String fullWordDataListJSON = gson.toJson(l);
        Log.i("WordArchive convertToJSON", "fullWordDataJSON: " + fullWordDataListJSON);
        return fullWordDataListJSON;
    }

    private TreeMap convertFromJSONString(String jsonStr) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TreeMap fullWordData = gson.fromJson(jsonStr, TreeMap.class);
        Log.i("WordArchive convertFromJSONString", "fullWordData: " + fullWordData.toString());
        return fullWordData;
    }
	
	// saving both [current date, all words added on that date (without their definitions)] and [word, full data on word]
	public boolean saveToArchive (LinkedList<TreeMap> fullWordDataList) {
		//TreeSet<String> allWords = new TreeSet<String>();
		SharedPreferences definitionArchive = c.getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, 0);
		SharedPreferences.Editor definitionArchiveEditor = definitionArchive.edit();
		Calendar cal = GregorianCalendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		
		// if datesArchive doesn't yet exist or is empty, create it and add the date of creation
		File f = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.DATES_WITH_WORDS_FILE + ".xml");
		System.out.println("syro datesWithWordsFile: /data/data/com.dcu.superword_a_day/shared_prefs/" + 
		Constants.DATES_WITH_WORDS_FILE + ".xml");
		if (!f.exists() || f.length() == 0) {
			SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
			SharedPreferences.Editor datesArchiveEditor = datesArchive.edit();
            datesArchiveEditor.putString("dateOfCreation", dateFormat.format(cal.getTime()));
            datesArchiveEditor.commit();
			System.out.println("syro WordArchive DATESARCHIVE -1 (inside if statement): " + datesArchive.toString());
		}
        ////////////////////////////////////////////////////////////////////////////////////////////

		SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		System.out.println("syro WordArchive DATESARCHIVE -1: " + datesArchive.toString());
		SharedPreferences.Editor datesArchiveEditor = datesArchive.edit();

        TreeMap fullWordData;
        TreeSet<String> allWords = new TreeSet<String>();
        String fullWordDataJSON, word;
        while(!fullWordDataList.isEmpty()) {
            fullWordData = fullWordDataList.remove();
            word = (String) fullWordData.get("word");
            allWords.add(word);
            fullWordDataJSON = convertToJSON(fullWordData);
            definitionArchiveEditor.putString(word, fullWordDataJSON);
        }

        Log.i("WordArchive", "saveToArchive: " + dateFormat.format(cal.getTime()));
        Log.i("WordArchive", "saveToArchive ALLWORDS: " + allWords.toString());

        datesArchiveEditor.putStringSet(dateFormat.format(cal.getTime()), allWords);
        /*
		LinkedList<String> definition;
		String word;
		String definitionPart;
		while(!w.isEmpty()) {
			TreeSet<String> definitionParts = new TreeSet<String>();
			definition = w.remove();
			word = definition.remove();
			allWords.add(word);
			while(!definition.isEmpty()) {
				definitionPart = definition.remove();
				definitionParts.add(definitionPart);
				Log.i("WordArchive", "Definition.toString(): " + definition.toString());
			}
			System.out.println("syro wordArchive: " + word + " " + definitionParts.toString());
			editor.putStringSet(word, definitionParts);
		}
		System.out.println("syro saveToArchive: " + dateFormat.format(cal.getTime()));
		System.out.println("syro saveToArchive ALLWORDS: " + allWords.toString());
		editor2.putStringSet(dateFormat.format(cal.getTime()), allWords);
		*/

        definitionArchiveEditor.commit();
        datesArchiveEditor.commit();
		return true;
	}
	
	// For testing purposes, used by WordArchiveDriverActivity
	public boolean saveToArchive (LinkedList<TreeMap> fullWordDataList, String date) {
		//TreeSet<String> allWords = new TreeSet<String>();
		SharedPreferences definitionArchive = c.getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, 0);
		SharedPreferences.Editor definitionArchiveEditor = definitionArchive.edit();
		
		// if datesArchive doesn't yet exist or is empty, add the date of creation after it's created automatically
		File f = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.DATES_WITH_WORDS_FILE + ".xml");
		System.out.println("syro datesWithWordsFile: /data/data/com.dcu.superword_a_day/shared_prefs/" + 
		Constants.DATES_WITH_WORDS_FILE + ".xml");
		if (!f.exists() || f.length() == 0) {
			SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
			SharedPreferences.Editor datesArchiveEditor = datesArchive.edit();
			Log.i("WordArchive", "WADA date of creation: " + date);
            datesArchiveEditor.putString("dateOfCreation", date);
            datesArchiveEditor.commit();
			System.out.println("syro WordArchive DATESARCHIVE -1 (inside if statement): " + datesArchive.toString());
		}
        /////////////////////////////////////////////////////////////////////////////////////

		SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		System.out.println("syro WordArchive DATESARCHIVE -1: " + datesArchive.toString());
		SharedPreferences.Editor datesArchiveEditor = datesArchive.edit();


        TreeMap fullWordData;
        TreeSet<String> allWords = new TreeSet<String>();
        String fullWordDataJSON, word;
        while(!fullWordDataList.isEmpty()) {
            fullWordData = fullWordDataList.remove();
            word = (String) fullWordData.get("word");
            allWords.add(word);
            fullWordDataJSON = convertToJSON(fullWordData);
            definitionArchiveEditor.putString(word, fullWordDataJSON);
        }

        Log.i("WordArchive", "saveToArchive: " + date);
        Log.i("WordArchive", "saveToArchive ALLWORDS: " + allWords.toString());

        datesArchiveEditor.putStringSet(date, allWords);
        /*
		LinkedList<String> definition;
		String word;
		String definitionPart;
		while(!w.isEmpty()) {
			TreeSet<String> definitionParts = new TreeSet<String>();
			definition = w.remove();
			Log.i("WordArchive", "Definition.toString(): " + definition.toString());
			word = definition.remove();
			allWords.add(word);
			while(!definition.isEmpty()) {
				definitionPart = definition.remove();
				definitionParts.add(definitionPart);
				Log.i("WordArchive", "Definition.toString(): " + definition.toString());
			}
			System.out.println("syro wordArchive: " + word + " " + definitionParts.toString());
			definitionArchiveEditor.putStringSet(word, definitionParts);
		}
		System.out.println("syro saveToArchive: " + date);
		System.out.println("syro saveToArchive ALLWORDS: " + allWords.toString());
		datesArchiveEditor.putStringSet(date, allWords);
        */

        definitionArchiveEditor.commit();
        datesArchiveEditor.commit();
		return true;
	}
	
	// returns all words (with their full word data) that were added on a specific date
	public LinkedList<TreeMap> loadFromArchive(String date) {
        LinkedList<TreeMap> fullWordDataList = new LinkedList<TreeMap>();
        SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
        Set<String> allWords = new HashSet<String>();
        Set<String> nullSet = null;
        Log.i("WordArchive loadFromArchive","date: " + date);
        allWords = (HashSet<String>) datesArchive.getStringSet(date, nullSet);
        // if any words were retrieved for the given date
        if(allWords != null) {
            Log.i("WordArchive allWords","allWords: " + allWords.toString());
            Iterator<String> it = allWords.iterator();
            String word, wordDataJSON;
            TreeMap fullWordData;
            SharedPreferences definitionArchive = c.getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, 0);
            while(it.hasNext()) {
                word = it.next();
                wordDataJSON = definitionArchive.getString(word, "");
                Log.i("WordArchive", "wordDataJSON: " + wordDataJSON);
                // converting the JSON String back to a TreeMap
                fullWordData = convertFromJSONString(wordDataJSON);
                fullWordDataList.add(fullWordData);
            }
        }

        /*
		LinkedList<LinkedList<String>> wordsAndDefinitions = new LinkedList<LinkedList<String>>();
		SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		Set<String> allWords = new HashSet<String>();
		Set<String> nullSet = null;
		Log.i("WordArchive loadFromArchive","date: " + date);
		allWords = (HashSet<String>) datesArchive.getStringSet(date, nullSet);
		// if any words were retrieved for the given date
		if(allWords != null) {
			Log.i("WordArchive allWords","allWords: " + allWords.toString());
			Iterator<String> it = allWords.iterator();
			String word;
			while(it.hasNext()) {
				LinkedList<String> thisWordAndDefinition = new LinkedList<String>();
				word = it.next();
				thisWordAndDefinition.add(word);
				SharedPreferences definitionArchive = c.getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, 0);
				HashSet<String> definitionParts = new HashSet<String>();
				definitionParts = (HashSet<String>) definitionArchive.getStringSet(word, nullSet);
				Log.i("WordArchive", "definitionParts: " + definitionParts.toString());
				Iterator<String> partsIterator = definitionParts.iterator();
				while(partsIterator.hasNext()) {
					thisWordAndDefinition.add(partsIterator.next());
				}
				wordsAndDefinitions.add(thisWordAndDefinition);
			}
		}
        */
		return fullWordDataList;
	}
	
	// called by TodaysWords to find out whether 'today's words' have already been scraped and saved
	public boolean checkForTodaysWords (int sharedPrefNoOfWords) {
		Calendar cal = GregorianCalendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		TreeSet<String> emptySet = new TreeSet<String>();
		Set<String> allWords = datesArchive.getStringSet(dateFormat.format(cal.getTime()), emptySet);
		Log.i("WordArchive checkForTodaysWords", allWords.toString());
		if(allWords.isEmpty()) {
			return false;
		}
		else if(allWords.size() < sharedPrefNoOfWords) {
            Log.i("WordArchive checkForTodaysWords", "SIZE: " + allWords.size()
					+ " & SHAREDPREFNOOFWORDS: " + sharedPrefNoOfWords);
			return false;
		}
		else return true;
	}
}
