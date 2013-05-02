package search.datastructure;

import java.util.ArrayList;


public class IndexEntry {
	String word;
	ArrayList<Posting> postings;
	
	public IndexEntry(String word, ArrayList<Posting> postings) {
		super();
		this.word = word;
		this.postings = postings;
	}

	@Override
	public String toString() {
		return word+":"+postings;
	}
		
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}

	public Posting getPosting(long docId){
		for(Posting p : getAllPostings()){
			if(p.getDocId()==docId)
				return p;
		}
		return null;
	}

	public ArrayList<Posting> getAllPostings() {
		return postings;
	}

	public void setPostings(ArrayList<Posting> postings) {
		this.postings = postings;
	}
}
