package search.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import search.datastructure.Page;

public class Parser extends DefaultHandler{
	private boolean flag;
	private String currentTag;
	private StringBuffer CurrentText=new StringBuffer();
        private Page currentPage;
	SAXParser sp;
        Index index;

	public Parser() throws ParserConfigurationException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
                sp =  spf.newSAXParser();
	}

	public void parseFile(String fileName, Index index) throws ParserConfigurationException {
                this.index=index;
                try {
			sp.parse(fileName, this);
		}catch(SAXException se) {
			se.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		currentTag = qName;
		if(qName.equalsIgnoreCase("page")){
			currentPage = new Page();
		} else if(qName.equalsIgnoreCase("title")){
			flag=true;
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		
		if(currentTag.equalsIgnoreCase("text")){
			CurrentText.append(ch,start,length);
		}else{
			CurrentText = new StringBuffer();
			CurrentText.append(ch,start,length);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equalsIgnoreCase("page")){
            try {
                //allPages.add(currentPage);
                index.insertPageToIndex(currentPage);
            } catch (IOException ex) {
                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            }
		}else if(qName.equalsIgnoreCase("title")){
			currentPage.setTitle(CurrentText.toString());
			//System.out.println("Title : "+ CurrentText);
		}else if(qName.equalsIgnoreCase("id")){
			if(flag){	// get the outer id only
				currentPage.setId(Long.parseLong(CurrentText.toString()));
				//System.out.println("ID : "+CurrentText);
			}
			flag=false;
		}else if(qName.equalsIgnoreCase("text")){
			currentPage.setText(CurrentText);
			//System.out.println("Text : "+ CurrentText);
		}
	}
}
