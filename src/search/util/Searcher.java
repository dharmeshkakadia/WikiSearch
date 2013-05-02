package search.util;

import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import search.datastructure.Posting;

public class Searcher extends Thread{
	//private static final int LIMIT = 10000;
	private String term;
	private String meta;
	TreeMap<String, String> secondaryIndex;
	TreeSet<Posting> postingsList = new TreeSet<Posting>();
	PostingReader reader;
	int threshold;
	
	public Searcher(String term, String meta, TreeMap<String, String> secondaryIndex, PostingReader reader, int threshold) {
		this.term=term;
		this.meta=meta;
		this.secondaryIndex=secondaryIndex;
		this.reader=reader;
		this.threshold=threshold;
	}
	
	@Override
	public void run() {
		Entry<String, String> fromEntry = secondaryIndex.floorEntry(term);
		try {
			if(fromEntry.getKey().equals(term)){
				postingsList = reader.readPosting(Long.parseLong(fromEntry.getValue(),16),meta);
			}else{
				postingsList = reader.readPosting(term, meta, Long.parseLong(fromEntry.getValue(),16),threshold);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	public String getTerm() {
		return term;
	}

	public TreeSet<Posting> getPostingsList() {
		return postingsList;
	}

	public String getMeta() {
		return meta;
	}
}
