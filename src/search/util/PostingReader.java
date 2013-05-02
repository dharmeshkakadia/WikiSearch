package search.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeSet;

import search.datastructure.Posting;

public class PostingReader {
	RandomAccessFile in;
	
	public PostingReader(RandomAccessFile in) {
		this.in=in;
	}
	
	public TreeSet<Posting> readPosting(long fromPosition, String meta) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(in.getFD()));
		synchronized (in) {
			in.seek(fromPosition);
			
			while(reader.read() != ';'){}
			
			return readTheLine(reader, meta);
		}
	}
	
	
	public TreeSet<Posting> readPosting(String term, String meta, long fromPosition, int threshold) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(in.getFD()));
		int ch;
		StringBuffer word= new StringBuffer();
		
		synchronized (in) {
			in.seek(fromPosition);
			
			while((ch=reader.read()) != ';'){
				word.append((char)ch);
			}
			if(word.toString().equals(term)){
				return readTheLine(reader, meta);
			}else{
				int count=threshold;
				while(count>0){
					while(reader.read() != '\n'){}
					
					word = new StringBuffer();
					while((ch=reader.read()) != ';'){
						word.append((char)ch);
					}
					if(word.toString().equals(term)){
						return readTheLine(reader, meta);
					}
					count--;
				}
				
			}
			return new TreeSet<Posting>();
		}
	}
	
	private TreeSet<Posting> readTheLine(BufferedReader reader, String meta) throws NumberFormatException, IOException{
		TreeSet<Posting> postings = new TreeSet<Posting>();
		long docId = 0;
		StringBuffer buf = new StringBuffer();
		boolean isNumber = true;
		int ch;
		String meta1 = null;
		while((ch=reader.read()) != '\n'){
			if(ch==';'){
				if(isNumber){
					docId = Long.parseLong(buf.toString());
					isNumber=false;
				}else{
					meta1=buf.toString();
					if(meta1.contains(meta.charAt(0)+"")){
						postings.add(new Posting(docId,meta1,true));
					}
    				isNumber=true;
				}
				buf = new StringBuffer();
			}else{					
				buf.append((char)ch);
			}
		}
		postings.add(new Posting(docId,meta1,false));
		return postings;
	}
}
