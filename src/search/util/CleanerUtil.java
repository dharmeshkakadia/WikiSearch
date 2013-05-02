package search.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanerUtil {
	private static final String STOPWORD_FILE = "/media/NewVolume/stopwords";
	static final Pattern numberPattern = Pattern.compile("\\d");
	//static final Pattern nonAsciiPattern = Pattern.compile("[^\\p{ASCII}]");
	static final Pattern nonWordPattern = Pattern.compile("[^\\w]");
	HashSet<String> stopWords = new HashSet<String>();
	
	public CleanerUtil() {
		BufferedReader reader=null;
		try{
			reader = new BufferedReader(new FileReader(STOPWORD_FILE));
			stopWords.addAll( Arrays.asList(reader.readLine().split(",")));
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("stopwords File Not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader != null)
				try{
					reader.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	public boolean isNonWord(String word) {
		Matcher matcher = nonWordPattern.matcher(word);
		if(matcher.find())
			return true;
		else
			return false;
	  }
	
	public boolean isValid(String word) {
		/*
		Matcher matcher = nonWordPattern.matcher(word);
		if(word.length() <= 2)
			return false;
		else if(matcher.find())
			return false;
		else
			return true;
		*/
		if(word.length() <= 2  || isNumber(word) || isStopWord(word) || isNonWord(word) ){
			return false;
		}
		return true;
		
	}
	
	public boolean isNumber(String word){
		Matcher matcher = numberPattern.matcher(word);
		if(matcher.find())
			return true;
		else
			return false;
	}

	public boolean isStopWord(String word){
		return stopWords.contains(word);
	}

	public String[] getStopWords() {
		return (String[]) stopWords.toArray();
	}
	
}
