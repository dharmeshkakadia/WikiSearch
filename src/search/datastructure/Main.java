package search.datastructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import search.util.Index;
import search.util.Merger;
import search.util.Parser;

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
    	// Index
    	
    	//Long startTime = System.currentTimeMillis();
        /*
    	File folder = new File(args[0]); System.out.println("Input Folder : "+folder); Index index = new Index(args[1]);
        File[] filenames = folder.listFiles(); Arrays.sort(filenames, new Comparator<File>() {
        	@Override 
        	public int compare(File f1, File f2) {
        	 	return f1.getName().compareTo(f2.getName()); 
        	 } 
        	});

        Parser parser = new Parser();

        for (File file : filenames) { 
        	System.out.println("Processing file "+file.getAbsolutePath());
        	parser.parseFile(file.getAbsolutePath(),index);
        }
        index.writeTitleToDisk(); 
        index.writeToDisk(); 
        Long endTime = System.currentTimeMillis(); 
        System.out.println("Time Taken : " + (endTime - startTime));
    	 */

    	/*
        // indexMerger 
        ArrayList<String> files = new ArrayList<String>();
        File folder = new File(args[0]); File[] filenames = folder.listFiles();
        for (File file : filenames) { 
        	files.add(file.getAbsolutePath()); 
        }

        Merger merger = new Merger(); 
        merger.merge(files, args[1]); 
        Long endTime = System.currentTimeMillis(); 
        System.out.println("Time Taken : " + (endTime - startTime));
		*/
    	
    	/*
		//titleMerger
		ArrayList<String> files = new ArrayList<String>();
		File folder = new File(args[0]);
		File[] filenames = folder.listFiles();
				
		for (File file : filenames) {
			files.add(file.getAbsolutePath());
		}

		Merger merger = new Merger();
		merger.mergeTitles(files, args[1]);
		
		Long endTime  = System.currentTimeMillis();
		System.out.println("Time Taken : " + (endTime - startTime));
		*/

    	// SecIndex Creation
        /*
        SecondaryIndex temp = new SecondaryIndex();
        temp.creatSecondaryIndex(args[0],args[1]);
        Long endTime = System.currentTimeMillis(); 
        System.out.println("Time Taken : " + (endTime - startTime));
         */
        
    	/*
        // Search
        if(args.length < 6){
        	showHelp();
        	return;
        }
    	SecondaryIndex secondaryIndex = new SecondaryIndex(args[2], args[3], args[4], Integer.parseInt(args[5]));
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String line;
        //while(true){
        	//System.out.println("Want to search ?");
        	//String input=System.console().readLine();
        	//if(input.equalsIgnoreCase("y")){
            	try {
                    reader = new BufferedReader(new FileReader(args[0]));
                    writer = new BufferedWriter(new FileWriter(args[1]));
                    while ((line = reader.readLine()) != null && !line.equals("")) {
                        writer.append(secondaryIndex.search(line).append("\n"));
                    }
                    reader.close();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

        	//}else if(input.equalsIgnoreCase("n")){
        	//	break;
        	//}
        //}
         */
    	if(args.length < 7){
        	showHelp();
        	return;
        }
    	SecondaryIndex secondaryIndex = new SecondaryIndex(args[2], args[3], args[4], Integer.parseInt(args[5]));
    	System.out.println(secondaryIndex.search(args[6]));
    }

	private static void showHelp() {
		System.out.println("Usage: java -jar WikiSearch.jar InputFile OutputFile SecondaryIndexPath MainIndexPath ForwardTitleIndexPath SparseIndexCounter(Default:5) SearchTerms(with meta)");
		System.out.println("For Example : java -jar WikiSearch.jar InputFile OutputFile SecondaryIndexPath MainIndexPath ForwardTitleIndexPath 5 \"t: India\"");
	}
}