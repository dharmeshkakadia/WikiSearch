package search.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import search.datastructure.Page;
import search.datastructure.Posting;

public class Index {
	private final int BUFER_SIZE = 10000;
	private final int threshold=100000;
	public String outputFolder = "/home/dharmesh/Desktop/output";
	
	private StringBuffer content = new StringBuffer(BUFER_SIZE);
	private static int fileId = 1;
	private static int titleFileId=1;
	private SortedMap<String, ArrayList<Posting>> map = new TreeMap<String, ArrayList<Posting>>();
	private SortedMap<Long,String> titleMap = new TreeMap<Long, String>();
	
	private ArrayList<String> indexFileNames = new ArrayList<String>();
	private ArrayList<String> titleFileNames = new ArrayList<String>();
	CleanerUtil cleaner = new CleanerUtil();;
	
	public Index(String folder) {
		outputFolder = folder;
	}

	public void insertPageToIndex(Page onePage) throws IOException {
		final String TOKEN_STRING = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";

		//insertTitle(onePage.getId(), onePage.getTitle());
		titleMap.put(onePage.getId(), onePage.getTitle());

		if(titleMap.size() >= threshold){
			writeTitleToDisk();
			titleMap.clear();
		}

		// insert page title
		StringTokenizer titleTokenizer = new StringTokenizer(onePage.getTitle(),TOKEN_STRING);
		while(titleTokenizer.hasMoreTokens()){
			 String word = getStemmedWord(titleTokenizer.nextToken().trim());
			if(cleaner.isValid(word)){
				insert(word, new Posting(onePage.getId(),"1t"));
			}
		}

		// insert page content
		StringTokenizer textTokenizer = new StringTokenizer(onePage.getPlainText(),TOKEN_STRING);
		while(textTokenizer.hasMoreTokens()){
			String word = getStemmedWord(textTokenizer.nextToken().trim());
			if(cleaner.isValid(word)){
					insert(word, new Posting(onePage.getId(),"1c"));
			}
		}

		// insert InfoBox
		String infoBoxString = onePage.getInfoBox();
		if(infoBoxString != null){
			StringTokenizer infoBoxTokenizer = new StringTokenizer(infoBoxString,TOKEN_STRING);
			while(infoBoxTokenizer.hasMoreTokens()){
				String word = getStemmedWord(infoBoxTokenizer.nextToken().trim());
				if(cleaner.isValid(word)){
					insert(word, new Posting(onePage.getId(),"1i"));
				}
			}
		}

		// insert OutLinks
		for(String outLinkString : onePage.getLinks()){
			StringTokenizer outLinkTokenizer = new StringTokenizer(outLinkString,TOKEN_STRING);
			while(outLinkTokenizer.hasMoreTokens()){
				String word = getStemmedWord(outLinkTokenizer.nextToken().trim());
				if(cleaner.isValid(word)){
					insert(word, new Posting(onePage.getId(),"1o"));
				}
			}
		}

		// insert catagaroies
		for(String catagoryString : onePage.getCategories()){
			StringTokenizer catagoryTokenizer = new StringTokenizer(catagoryString,TOKEN_STRING);
			while(catagoryTokenizer.hasMoreTokens()){
				String word = getStemmedWord(catagoryTokenizer.nextToken().trim());
				if(cleaner.isValid(word)){
					insert(word, new Posting(onePage.getId(),"1g"));
				}
			}
		}
		
		if(map.size() >= threshold){
			writeToDisk();
			map.clear();
			//Runtime.getRuntime().gc();
		}
	}

	public String getStemmedWord(String word){
		Stemmer stemmer = new Stemmer();
		stemmer.add(word.toLowerCase().toCharArray(), word.length());
		stemmer.stem();
		return stemmer.toString();
	}
	
	public void insert(String word, Posting posting) {
		ArrayList<Posting> oldPosting = map.get(word);
		if(oldPosting == null){	// First time the word has occured in collection
			ArrayList<Posting> temp = new ArrayList<Posting>();
			temp.add(posting);
			map.put(word, temp);
		}else{	// word is already there in collection
			Posting p = oldPosting.get(oldPosting.size()-1);
			if(p.getDocId() == posting.getDocId()){
				p.mergePosting(posting);
				oldPosting.set(oldPosting.size()-1, p);
			}else{
				oldPosting.add(posting); // add new Posting
			}
			map.put(word, oldPosting);
		}
	}
	
	public void writeToDisk() throws IOException{
		final String filename = outputFolder+"/" +fileId;
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
		content = new StringBuffer(BUFER_SIZE);
		for(Entry<String, ArrayList<Posting>> entry : map.entrySet()){
			content.append(entry.getKey());
			//ArrayList<Posting> ps = new ArrayList<Posting>();
			//ps.addAll(entry.getValue());
			ArrayList<Posting> ps = entry.getValue();
			Collections.sort(ps);
			//content.append(ps.size());
			for(Posting p : ps){
				content.append(p);
			}
			content.append('\n');
		}

		out.append(content);
		fileId++;
		out.close();	
		indexFileNames.add(filename);
		System.out.println("Written Index file : " + filename);
	}

	public void writeTitleToDisk() throws IOException {
		final String filename = outputFolder+"/t" +titleFileId;
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
		content = new StringBuffer(BUFER_SIZE);
		for(Entry<Long,String> entry : titleMap.entrySet()){
			content.append(entry.getKey()).append(';').append(entry.getValue()).append('\n');
		}
		out.append(content);
		titleFileId++;
		out.close();
		titleFileNames.add(filename);
		System.out.println("Written Title-Index file : " + filename);
	}

	public ArrayList<String> getIndexFileNames() {
		return indexFileNames;
	}

	public ArrayList<String> getTitleFiles() {
		return titleFileNames;
	}
}
