package search.datastructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import search.util.CleanerUtil;
import search.util.PostingReader;
import search.util.Searcher;
import search.util.Stemmer;

public class SecondaryIndex {
	TreeMap<String, String> secondaryIndex;
	RandomAccessFile titles;
	public RandomAccessFile index;
	private final int RESULT_SIZE = 10;
	private int THRESHOLD = 5;
	CleanerUtil cleaner = new CleanerUtil();;

	public SecondaryIndex() {
	}
	
	public SecondaryIndex(String secondaryIndexFileName,String finalIndex, String titleFileName, int threshold) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(secondaryIndexFileName)));
		String line;
		int count=0;
		secondaryIndex = new TreeMap<String, String>();
		THRESHOLD=threshold;
		
		while((line = in.readLine()) != null){		
			if(count >= THRESHOLD){
				int i = line.indexOf(';');
				secondaryIndex.put(line.substring(0, i),line.substring(i+1));
				count =0;continue;
			}
			count++;
		}
		//System.out.println(secondaryIndex.size());
		in.close();

		titles = new RandomAccessFile(titleFileName,"r");
		index = new RandomAccessFile(finalIndex,"r");
	}

	public void cleanUp() throws IOException{
		titles.close();
		index.close();
	}
	
	public StringBuffer search(String userInput) throws IOException, InterruptedException{
		StringBuffer result = new StringBuffer();
		ArrayList<String> queryTerms;
		Long startTime  = System.currentTimeMillis();
		HashMap<String, TreeSet<Posting>> resultMap = new HashMap<String, TreeSet<Posting>>();
		Searcher[] t = new Searcher[7];
		int j=0;
		PostingReader reader = new PostingReader(index);
		queryTerms = parseQuery(userInput);
		
		for(String termAndMeta : queryTerms){
			String[] terms = termAndMeta.split(":");
			t[j] = new Searcher(terms[1],terms[0],secondaryIndex,reader, THRESHOLD);
			t[j].start();
			j++;
		}
		TreeSet<Posting> temp;
		for(int i=0;i<j;i++){
			t[i].join();
			temp = t[i].getPostingsList();
			resultMap.put(t[i].getTerm()+t[i].getMeta(),temp);
		}
		//System.out.println(resultMap.size());
		//System.out.println(queryTerms);
		//System.out.println("Result Map "+resultMap);

		TreeSet<Posting> intersectionPostings = findIntersection(resultMap,resultMap.keySet());
		int intersectionSize = intersectionPostings.size();

		TreeSet<Posting> level2Intersection = new TreeSet<Posting>();
		if(intersectionSize < RESULT_SIZE){
			for(String key1 : resultMap.keySet()){
				Set<String> remainingKeys = new HashSet<String>();
				remainingKeys.addAll(resultMap.keySet());
				Set<Posting> intersection = new HashSet<Posting>();
				remainingKeys.remove(key1);
				//System.out.println("remianing Keys " + remainingKeys);
				intersection=findIntersection(resultMap, remainingKeys);
				//System.out.println("level2 results "+level2Intersection);
				level2Intersection.addAll(intersection);
				if((level2Intersection.size() + intersectionSize) >= RESULT_SIZE)
					break;
			}
			level2Intersection.removeAll(intersectionPostings);
		}
		
		TreeSet<Posting> level3Intersection = new TreeSet<Posting>();
		int level2IntersectionSize=level2Intersection.size();
		if((intersectionSize + level2IntersectionSize) < RESULT_SIZE){
			for(String key1 : resultMap.keySet()){
				//System.out.println("Key1 "+key1);
				for(String key2 : resultMap.keySet()){
					//System.out.println("Key2 "+key2);
					Set<String> remainingKeys = new HashSet<String>();
					remainingKeys.addAll(resultMap.keySet());
					remainingKeys.remove(key1);
					remainingKeys.remove(key2);
					Set<Posting> intersection = new HashSet<Posting>();
					//System.out.println("remianing Keys in level 3 " + remainingKeys);
					intersection=findIntersection(resultMap, remainingKeys);
					//System.out.println("level3 results "+intersection);
					level3Intersection.addAll(intersection);
					if((level3Intersection.size()+ level2IntersectionSize + intersectionSize + 20) >= RESULT_SIZE)
						break;
				}
			}
			level3Intersection.removeAll(level2Intersection);
			level3Intersection.removeAll(intersectionPostings);
		}
		
		//System.out.println("INtersection : " + intersectionPostings);
		//System.out.println("level2 results "+level2Intersection);
		TreeSet<Posting> unionPostings = new TreeSet<Posting>();
				
		if((intersectionSize + level2IntersectionSize + level3Intersection.size()) < RESULT_SIZE){
			for(String key : resultMap.keySet()){
				if((unionPostings.size() + intersectionSize + level3Intersection.size()) < RESULT_SIZE){
					TreeSet<Posting> currentPostings = resultMap.get(key);
					for(Posting q : currentPostings){
						if(!intersectionPostings.contains(q))
							unionPostings.add(q);
					}
				}
			}
		}
		//System.out.println(unionPostings);
		Posting[] arr1 = null;
		if(unionPostings.size() >0){

			unionPostings=rank(unionPostings);
			arr1=(Posting[]) unionPostings.toArray(new Posting[unionPostings.size()]);
			Arrays.sort(unionPostings.toArray(arr1), new Comparator<Posting>() {
			    @Override
			    public int compare(Posting p1, Posting p2) {
			    	double d1= p1.getScore();
			    	double d2= p2.getScore();
			    	if(d1< d2)
			    		return 1;
			    	else if(d1>d2)
			    		return -1;
			    	else
			    		return 0;
			    }
			}
			);
			
		}
				
		// do set operations
		// append the results
		Long endTime  = System.currentTimeMillis();
		result.append("Time: ").append(endTime-startTime).append("ms");
		int resultCount=1;
				
		for(Posting p : intersectionPostings){
			if(resultCount++>RESULT_SIZE)
				return result;
			result.append("\n").append(p.docId).append("\t").append(getTitle(p.docId));
		}
		
		for(Posting p : level2Intersection){
			if(resultCount++>RESULT_SIZE)
				return result;
			result.append("\n").append(p.docId).append("\t").append(getTitle(p.docId));
		}
		
		for(Posting p : level3Intersection){
			if(resultCount++>RESULT_SIZE)
				return result;
			result.append("\n").append(p.docId).append("\t").append(getTitle(p.docId));
		}
		
		for(int var=0;var < arr1.length;var++){
			if(resultCount++>RESULT_SIZE)
				return result;
			Posting p = arr1[var];
			result.append("\n").append(p.docId).append("\t").append(getTitle(p.docId));
		}
		
//		for(Posting p : unionPostings){
//			if(resultCount++>RESULT_SIZE)
//				return result;
//			result.append("\n----").append(p.score).append("%").append(p).append("\t").append(getTitle(p.docId));
//		}
		
        while(resultCount++ <= RESULT_SIZE)
        	result.append("\nNA");
		
		return result;
	}
	
	public void creatSecondaryIndex(String indexFile, String fileName) throws IOException{
		long offset = 0;
		BufferedReader indexReader = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile)));
		BufferedWriter secIndexWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
		String line;
		
		while((line=indexReader.readLine())!=null){
			int i = line.indexOf(';');
			StringBuffer s = new StringBuffer();
			s.append(line.substring(0,i)).append(';').append(Long.toHexString(offset)).append('\n');
			offset+=line.length()+1;
			secIndexWriter.append(s);
		}
		indexReader.close();
		secIndexWriter.close();
	}
	
	public String getTitle(long docId) throws IOException{
		return readTitle(titles, docId, 0, titles.length());
	}
	
	public String readTitle(RandomAccessFile in, long docId, long fromPosition, long toPosition) throws IOException {
		if(fromPosition >= toPosition) return null;
		in.seek(fromPosition);
		in.readLine();
		String line = in.readLine();
		String tokens[] = line.split(";");
		long start = Long.parseLong(tokens[0]);
		if(start == docId){
			return tokens[1];
		}else{
			long middlePosition = (fromPosition+toPosition)/2;
			in.seek(middlePosition);
			in.readLine();
			String middleString = in.readLine();
			String temp[] = middleString.split(";");
			long middle = Long.parseLong(temp[0]);
			if(middle == docId)
				return temp[1]; 
			else if(docId < middle)
				return readTitle(in, docId, fromPosition, middlePosition);
			else
				return readTitle(in, docId, middlePosition+middleString.length()+1, toPosition);
		}
	}

	public ArrayList<String> parseQuery(String userInput){
		ArrayList<String> result = new ArrayList<String>();
		userInput = userInput.replaceAll("\\s+", " ").toLowerCase().replace("c:", "g:").replace("b:", "c:");
		String tokens[] = userInput.split(" ");
		for(int i=0;i<tokens.length -1;i=i+2){
			Stemmer stemmer = new Stemmer();
			stemmer.add(tokens[i+1].toCharArray(), tokens[i+1].length());
			stemmer.stem();
			String word = stemmer.toString();
			if(cleaner.isValid(word)){
				result.add(tokens[i]+word);
			}else {
				System.out.println("Ignoring word "+word);
			}
		}
		
		return result;
	}
	
	public TreeSet<Posting> rank(TreeSet<Posting> postings){
		int df=postings.size();
		for(Posting p : postings){
			p.setScore((double)p.calculateTf()/df);
		}
		
		return postings;
	}
	
	public ArrayList<String> giveAllWords(String str, String c){
		String tokens[]=str.split(" ");
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0;i<tokens.length;i=i+1){
			if(tokens[i].length()>2){
				Stemmer stemmer = new Stemmer();
				stemmer.add(tokens[i].toCharArray(), tokens[i].length());
				stemmer.stem();
				String word = stemmer.toString();
				result.add(c+":"+word);	
			}
		}
		return result;
	}
	
	public TreeSet<Posting> findIntersection(HashMap<String, TreeSet<Posting>> resultMap, Set<String> keys){
		TreeSet<Posting> intersectionPostings = new TreeSet<Posting>(); 	
		boolean firstTime = true;
		for(String key : keys){
			Set<Posting> r = resultMap.get(key);
			if (firstTime) {
				intersectionPostings.addAll(r);
				firstTime=false;
			}else{
				Set<Posting> removeSet = new HashSet<Posting>();
				for(Posting p : intersectionPostings){
					boolean found = false;
					for(Posting q : r){
						if(p.docId == q.docId){
							found=true;
							break;
						}
					}
					if(!found)
						removeSet.add(p);
				}
				intersectionPostings.removeAll(removeSet);
			}
		}
		return intersectionPostings;
	}
}
