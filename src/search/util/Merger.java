package search.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import search.datastructure.IndexEntry;
import search.datastructure.Posting;
import search.datastructure.TitleIndex;

public class Merger {
	final int BUFER_SIZE = 100000;
	final int threshold = 100000;
	int fileId = 1;
	//static long offset=0;
	//StringBuffer secIndex = new StringBuffer();
	//final String secIndexFileName;="/home/dharmesh/Desktop/output/secIndex";
	BufferedWriter secIndexWriter;
	
	public Merger() throws FileNotFoundException {
		//secIndexWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(secIndexFileName)));
	}
	
	
	public void merge(ArrayList<String> files,String outputFileName){
		int nextCount = 0;
		
		if(files.size() == 3){
			String tempFileName = files.get(1)+"_";
			merge(files.get(0),files.get(1),tempFileName);
			merge(files.get(2),tempFileName,outputFileName);
		}else if(files.size() == 2){
			merge(files.get(0),files.get(1),outputFileName);
		}else if(files.size() == 1){
			return;
		}else{
			ArrayList<String> newFiles = new ArrayList<String>();
			while(nextCount < files.size()-1){
				String tempFileName = files.get(nextCount+1)+"_";
				merge(files.get(nextCount),files.get(nextCount+1),tempFileName);
				newFiles.add(tempFileName);
				nextCount=nextCount+2;
			}
			if(files.size()%2 ==1)
				newFiles.add(files.get(nextCount));
			merge(newFiles,outputFileName);
		}
	}
	
	public void merge(String f1, String f2,String f3){
		try {
			StringBuffer buffer = new StringBuffer(BUFER_SIZE);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(f1)));
			BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(f2)));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f3)));
			IndexEntry p1=null;
			IndexEntry p2=null;
			IndexEntry mergedIndexEntry=null;

			while(true){
				if(p1==null){
					p1 = readPosting(in1);
				}
				if(p2==null){
					p2 = readPosting(in2);
				}
				
				if(p1==null||p2==null){
					break;
				}

				if(p1.getWord().equalsIgnoreCase(p2.getWord())){
					mergedIndexEntry = mergeIndexEntries(p1,p2);
					buffer=writeToFile(mergedIndexEntry,buffer,out);
					p1=null;
					p2=null;
				}else if(p1.getWord().compareToIgnoreCase(p2.getWord()) < 1){
					buffer=writeToFile(p1,buffer,out);
					p1=null;
				}else{
					buffer=writeToFile(p2,buffer,out);
					p2=null;
				}
				
			}
			
			if(p2==null)
				while((p1 = readPosting(in1))!=null){
					buffer=writeToFile(p1,buffer,out);
				}
			
			if(p1==null)
				while((p2 = readPosting(in2))!=null){
					buffer=writeToFile(p2,buffer,out);
				}
			
			if(buffer.length()>0){
				out.append(buffer);
				buffer = new StringBuffer(BUFER_SIZE);
			}
			
			System.out.println("Merged " + f1 + " and "+f2 + " to "+ f3);
			in1.close();
			in2.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public IndexEntry mergeIndexEntries(IndexEntry p1, IndexEntry p2) {
		ArrayList<Posting> temp = new ArrayList<Posting>();
		temp.addAll(p1.getAllPostings());
		temp.addAll(p2.getAllPostings());
		Collections.sort(temp);
		p1.setPostings(temp);
		return p1;
	}

	private StringBuffer writeToFile(IndexEntry entry, StringBuffer buffer ,BufferedWriter out) throws IOException {
        buffer.append(entry.getWord());
		for(Posting p : entry.getAllPostings()){
			buffer.append(p);
		}
		buffer.append('\n');

		if(buffer.length() >= threshold){
			out.append(buffer);
			buffer = new StringBuffer(BUFER_SIZE);
		}
		
		return buffer;
	}

	public void mergeTitles(ArrayList<String> files,String outputFileName){
		int nextCount = 0;
		
		if(files.size() == 3){
			String tempFileName = files.get(0)+ "_";
			mergeTitles(files.get(0),files.get(1),tempFileName);
			mergeTitles(files.get(2),tempFileName,outputFileName);
		}else if(files.size() == 2){
			mergeTitles(files.get(0),files.get(1),outputFileName);
		}else if(files.size() == 1){
			return;
		}else{
			ArrayList<String> newFiles = new ArrayList<String>();
			while(nextCount < files.size()-1){
				String tempFileName = files.get(nextCount)+ "_";
				mergeTitles(files.get(nextCount),files.get(nextCount+1),tempFileName);
				newFiles.add(tempFileName);
				nextCount=nextCount+2;
			}
			if(files.size()%2 ==1)
				newFiles.add(files.get(nextCount));
			mergeTitles(newFiles,outputFileName);
		}
	}
	
	public void mergeTitles(String f1, String f2,String f3){
		StringBuffer titleBuffer = new StringBuffer(BUFER_SIZE);
		try {
			BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(f1)));
			BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(f2)));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f3)));
			TitleIndex p1=null;
			TitleIndex p2=null;
			
			while(true){
				if(p1==null){
					p1 = readTitleIndex(in1);
				}
				if(p2==null){
					p2 = readTitleIndex(in2);
				}
				
				if(p1==null||p2==null){
					break;
				}
				
				if(p1.getId() < p2.getId()){
					titleBuffer=writeTitleToFile(p1,titleBuffer,out);
					p1=null;
				}else{
					titleBuffer=writeTitleToFile(p2,titleBuffer,out);
					p2=null;
				}
			}
			
			if(p2==null)
				while((p1 = readTitleIndex(in1))!=null){
					titleBuffer=writeTitleToFile(p1,titleBuffer,out);
				}
			
			if(p1==null)
				while((p2 = readTitleIndex(in2))!=null){
					titleBuffer=writeTitleToFile(p2,titleBuffer, out);
				}
			
			if(titleBuffer.length()>0){
				out.append(titleBuffer);
				titleBuffer = new StringBuffer(10000);
			}
			
			System.out.println("Merged TitleFiles " + f1 + " and "+f2 + " to "+ f3);
			in1.close();
			in2.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public StringBuffer writeTitleToFile(TitleIndex entry, StringBuffer titleBuffer ,BufferedWriter out) throws IOException {
		titleBuffer.append(entry.getId()).append(';').append(entry.getTitle()).append('\n');
		
		if(titleBuffer.length() >= threshold){
			out.append(titleBuffer);
			titleBuffer = new StringBuffer(BUFER_SIZE);
		}
		return titleBuffer;
	}
	
	public static TitleIndex readTitleIndex(BufferedReader in) throws IOException {
		String line = in.readLine();
		if(line == null)
			return null;
		int i = line.indexOf(';');
		if(i<0)
			return null;
		return new TitleIndex(Long.parseLong(line.substring(0,i)),line.substring(i+1));
	}

	public static IndexEntry readPosting(BufferedReader in) throws IOException {
		String line = in.readLine();
		if(line == null){
			return null;
		}
		
		ArrayList<Posting> postings = new ArrayList<Posting>();
		String tokens[] = line.split(";");

		String word = tokens[0];
		for(int i=1;i<tokens.length-1;i=i+2){
			long docId = Long.parseLong(tokens[i]);
			postings.add(new Posting(docId,tokens[i+1]));
		}
		return new IndexEntry(word,postings);
	}
}
