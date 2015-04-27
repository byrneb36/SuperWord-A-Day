package com.dcu.superword_a_day;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class WordArchive {
	private Context c;
    
	public WordArchive (Context c) {
		this.c = c;
	}
	
	// saving both (current date, all words added on that date (without their definitions)) and (word, definition of word)
	public boolean saveToArchive (LinkedList<LinkedList<String>> w) {
		TreeSet<String> allWords = new TreeSet<String>();
		SharedPreferences definitionArchive = c.getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, 0);
		SharedPreferences.Editor editor = definitionArchive.edit();
		Calendar cal = GregorianCalendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		
		// if datesArchive doesn't yet exist or is empty, create it and add the date of creation
		File f = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.DATES_WITH_WORDS_FILE + ".xml");
		System.out.println("syro datesWithWordsFile: /data/data/com.dcu.superword_a_day/shared_prefs/" + 
		Constants.DATES_WITH_WORDS_FILE + ".xml");
		if (!f.exists() || f.length() == 0) {
			SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
			SharedPreferences.Editor editor2 = datesArchive.edit();
			editor2.putString("dateOfCreation", dateFormat.format(cal.getTime()));	
			editor2.commit();
			System.out.println("syro WordArchive DATESARCHIVE -1 (inside if statement): " + datesArchive.toString());
		}
		SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		System.out.println("syro WordArchive DATESARCHIVE -1: " + datesArchive.toString());
		SharedPreferences.Editor editor2 = datesArchive.edit();
		
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
		
		editor.commit();		
		editor2.commit();
		return true;
	}
	
	// For testing purposes, used by WordArchiveDriverActivity
	public boolean saveToArchive (LinkedList<LinkedList<String>> w, String date) {
		TreeSet<String> allWords = new TreeSet<String>();
		SharedPreferences definitionArchive = c.getSharedPreferences(Constants.DEFINITION_ARCHIVE_FILE, 0);
		SharedPreferences.Editor editor = definitionArchive.edit();
		
		// if datesArchive doesn't yet exist or is empty, add the date of creation after it's created automatically
		File f = new File("/data/data/com.dcu.superword_a_day/shared_prefs/" + Constants.DATES_WITH_WORDS_FILE + ".xml");
		System.out.println("syro datesWithWordsFile: /data/data/com.dcu.superword_a_day/shared_prefs/" + 
		Constants.DATES_WITH_WORDS_FILE + ".xml");
		if (!f.exists() || f.length() == 0) {
			SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
			SharedPreferences.Editor editor2 = datesArchive.edit();
			Log.i("WordArchive", "WADA date of creation: " + date);
			editor2.putString("dateOfCreation", date);	
			editor2.commit();
			System.out.println("syro WordArchive DATESARCHIVE -1 (inside if statement): " + datesArchive.toString());
		}
		SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		System.out.println("syro WordArchive DATESARCHIVE -1: " + datesArchive.toString());
		SharedPreferences.Editor editor2 = datesArchive.edit();
		
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
			editor.putStringSet(word, definitionParts);
		}
		System.out.println("syro saveToArchive: " + date);
		System.out.println("syro saveToArchive ALLWORDS: " + allWords.toString());
		editor2.putStringSet(date, allWords);
		
		editor.commit();		
		editor2.commit();
		return true;
	}
	
	// returns all words with their definitions added on a specific date
	public LinkedList<LinkedList<String>> loadFromArchive(String date) {
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
		return wordsAndDefinitions;
	}
	
	// called by TodaysWords to find out whether 'today's words' have already been scraped and saved
	public boolean checkForTodaysWords (int sharedPrefNoOfWords) {
		Calendar cal = GregorianCalendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		SharedPreferences datesArchive = c.getSharedPreferences(Constants.DATES_WITH_WORDS_FILE, 0);
		TreeSet<String> emptySet = new TreeSet<String>();
		Set<String> allWords = datesArchive.getStringSet(dateFormat.format(cal.getTime()), emptySet);
		System.out.println("syro checkForTodaysWords: " + allWords.toString());
		if(allWords.isEmpty()) {
			return false;
		}
		else if(allWords.size() < sharedPrefNoOfWords) {
			System.out.println("syro checkForTodaysWords SIZE: " + allWords.size()
					+ " & SHAREDPREFNOOFWORDS: " + sharedPrefNoOfWords);
			return false;
		}
		else return true;
	}
}
