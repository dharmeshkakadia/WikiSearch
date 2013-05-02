package search.datastructure;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Page {
	private String title = null;	
	private long id;
	private StringBuffer text = null;

	private ArrayList<String> catagaries = null;
	private ArrayList<String> links = null;
	private String infoBox = null;
	
	public Page(){
		
	}
	
	public Page(String title, long id, StringBuffer text) {
		this.text = text;
		this.title = title;
		this.id = id;
	}
	
	public ArrayList<String> getCategories() {
		if(catagaries == null)
			parseCategories();
		return catagaries;
	}

	public ArrayList<String> getLinks() {
		if(links == null) 
			parseLinks();
		return links;
	}

	private void parseCategories() {
		catagaries = new ArrayList<String>();
		Pattern catPattern = Pattern.compile("\\[\\[Category:(.*?)\\]\\]", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(text);
		while(matcher.find()) {
			String [] m1 = matcher.group(1).split("\\|");
			catagaries.add(m1[0]);
		}
	}
	
	private void parseLinks() {
		links = new ArrayList<String>();  
		
		Pattern catPattern = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(text);
		while(matcher.find()) {
			String [] temp = matcher.group(1).split("\\|");
			if(temp == null || temp.length == 0) continue;
			String link = temp[0];
			if(link.contains(":") == false) {
				links.add(link);
			}
		}
	}

	public String getPlainText() {
		String plainText = text.toString();
		//plainText = plainText.replaceAll("&gt;", ">");
		//plainText = plainText.replaceAll("&lt;", "<");
		plainText = plainText.replaceAll("<ref>.*?</ref>", "");
		plainText = plainText.replaceAll("</?.*?>", "");
		plainText = plainText.replaceAll("\\{\\{.*?\\}\\}", "");
		plainText = plainText.replaceAll("\\[\\[.*?:.*?\\]\\]", "");
		plainText = plainText.replaceAll("\\[\\[(.*?)\\]\\]", "");
		plainText = plainText.replaceAll("\\s(.*?)\\|(\\w+\\s)", " $2");
		plainText = plainText.replaceAll("\\[.*?\\]", " ");
		//plainText = plainText.replaceAll("\\'+", "");
		plainText = plainText.replaceAll("(?s)<!--.*?-->", "");
		plainText = plainText.replaceAll("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "");
		return removeCitation(plainText);
	}
	
	public String getInfoBox() {
		if(infoBox == null)
			infoBox = parseInfoBox();
		return infoBox;
	}

	private String parseInfoBox() {
		final String INFOBOX_PATTERN = "{{Infobox";
	    int startPos = text.indexOf(INFOBOX_PATTERN);
	    if(startPos < 0) return null;
	    int bracketCount = 2;
	    int endPos = startPos + INFOBOX_PATTERN.length();
	    for(; endPos < text.length(); endPos++) {
	      switch(text.charAt(endPos)) {
	        case '}':
	          bracketCount--;
	          break;
	        case '{':
	          bracketCount++;
	          break;
	        default:
	      }
	      if(bracketCount == 0) break;
	    }
	    if(endPos+1 >= text.length())
	    	return null;

	    String infoBoxText = text.substring(startPos, endPos+1);
	    infoBoxText = removeCitation(infoBoxText);
	    
	    infoBoxText = infoBoxText.replaceAll("&gt;", ">");
	    infoBoxText = infoBoxText.replaceAll("&lt;", "<");
	    infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
		infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");
	    return infoBoxText;
		/*
		final String INFOBOX_CONST_STR = "{{Infobox";
		int startPos = text.indexOf(INFOBOX_CONST_STR);

		if(startPos < 0)	// no infobox present
			return null;

		int endPos = text.lastIndexOf("}}", startPos+INFOBOX_CONST_STR.length());
		//System.out.println(startPos);
		//System.out.println(endPos);

		if(endPos+2 >= text.length()) 
			return null;

		String infoBoxText = text.substring(startPos, endPos+2);
		infoBoxText = stripCite(infoBoxText); // strip clumsy {{cite}} tags
		// strip any html formatting
		infoBoxText = infoBoxText.replaceAll("&gt;", ">");
		infoBoxText = infoBoxText.replaceAll("&lt;", "<");
		infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
		infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");
		return infoBoxText;
		*/
	}

	private String removeCitation(String text) {
		final String CITE_PATTERN = "{{cite";
	    int startPos = text.indexOf(CITE_PATTERN);
	    if(startPos < 0) return text;
	    int bracketCount = 2;
	    int endPos = startPos + CITE_PATTERN.length();
	    for(; endPos < text.length(); endPos++) {
	      switch(text.charAt(endPos)) {
	        case '}':
	          bracketCount--;
	          break;
	        case '{':
	          bracketCount++;
	          break;
	        default:
	      }
	      if(bracketCount == 0) break;
	    }
	    text = text.substring(0, startPos-1) + text.substring(endPos);
	    return removeCitation(text);   
	    /*
		String CITE_CONST_STR = "{{cite";
		int startPos = text.indexOf(CITE_CONST_STR);

		if(startPos < 0) 
			return text;

		int endPos = text.indexOf("}}",startPos+CITE_CONST_STR.length());
		// System.out.println(startPos + " : " + endPos);
		if(endPos > text.length())	// malformed
			return text;

		return stripCite(text.substring(0, startPos-1) + text.substring(endPos+1));
		*/
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public StringBuffer getText() {
		return text;
	}
	
	public void setText(StringBuffer text) {
		this.text = text;
	}

}
