package search.datastructure;

public class Posting implements Comparable<Posting>{
	long docId;
	//int tf;
	double score;
	String meta;
	long titleCount;
	long textCount;
	long infoBoxCount;
	long outlinkCount;
	long catagoryCount;
	
	public Posting(long docId) {
		this.docId=docId;
	}
        
        public Posting(long docId,String meta, boolean parseMeta) {
            this.docId=docId;
            this.meta=meta;
            if(parseMeta && meta!=null){

    			int startIndex=0;
    			int lastIndex;
    			
    			if(meta.contains("t")){
    				lastIndex = meta.indexOf('t');
    				titleCount = Long.parseLong(meta.substring(startIndex,lastIndex));
    				startIndex=lastIndex+1;
    			}
    			
    			if(meta.contains("c")){
    				lastIndex = meta.indexOf('c');
    				textCount = Long.parseLong(meta.substring(startIndex,lastIndex));
    				startIndex=lastIndex+1;
    			}
    			
    			if(meta.contains("i")){
    				lastIndex = meta.indexOf('i');
    				infoBoxCount = Long.parseLong(meta.substring(startIndex,lastIndex));
    				startIndex=lastIndex+1;
    			}
    			
    			if(meta.contains("o")){
    				lastIndex = meta.indexOf('o');
    				outlinkCount = Long.parseLong(meta.substring(startIndex,lastIndex));
    				startIndex=lastIndex+1;
    			}
    			
    			if(meta.contains("g")){
    				lastIndex = meta.indexOf('g');
    				catagoryCount = Long.parseLong(meta.substring(startIndex,lastIndex));
    				startIndex=lastIndex+1;
    			}
            }                
        }
	
	public Posting(long docId,String meta) {
		this.docId=docId;
		this.meta=meta;
		if(meta!=null){
			
			int startIndex=0;
			int lastIndex;
			
			if(meta.contains("t")){
				lastIndex = meta.indexOf('t');
				titleCount = Long.parseLong(meta.substring(startIndex,lastIndex));
				startIndex=lastIndex+1;
			}
			
			if(meta.contains("c")){
				lastIndex = meta.indexOf('c');
				textCount = Long.parseLong(meta.substring(startIndex,lastIndex));
				startIndex=lastIndex+1;
			}
			
			if(meta.contains("i")){
				lastIndex = meta.indexOf('i');
				infoBoxCount = Long.parseLong(meta.substring(startIndex,lastIndex));
				startIndex=lastIndex+1;
			}
			
			if(meta.contains("o")){
				lastIndex = meta.indexOf('o');
				outlinkCount = Long.parseLong(meta.substring(startIndex,lastIndex));
				startIndex=lastIndex+1;
			}
			
			if(meta.contains("g")){
				lastIndex = meta.indexOf('g');
				catagoryCount = Long.parseLong(meta.substring(startIndex,lastIndex));
				startIndex=lastIndex+1;
			}
		}
	}
	
	void incrementTitleCount(){
		titleCount++;
	}
	void incrementTextCount(){
		textCount++;
	}
	void incrementInfoBoxCount(){
		infoBoxCount++;
	}
	void incrementOutlinkCount(){
		outlinkCount++;
	}
	void incrementCatagoryCount(){
		catagoryCount++;
	}
	
	@Override
	public String toString() {
		return ";" + docId + ";" + meta;
		/*return ";" + docId + ";" + ( titleCount==0 ? "" : titleCount+"t") +
									( textCount==0 ? "" : textCount+"c") +
									( infoBoxCount==0 ? "" : infoBoxCount+"i") +
									( outlinkCount==0 ? "" : outlinkCount+"o") +
									( catagoryCount==0 ? "" : catagoryCount+"g"); */
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public long calculateTf() {
		return titleCount+textCount+infoBoxCount+outlinkCount+catagoryCount;
	}

	@Override
	public int hashCode(){
		return  (int)(docId ^ (docId>>> 32));
	}
	
	@Override
	public boolean equals(Object obj){
		/*
		if (this == obj) {
            return true;
        }*/

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        
        Posting otherObj = (Posting)obj;
        return docId==otherObj.docId;
	}
	
	@Override
	public int compareTo(Posting o) {
		if(this.docId == o.docId){
			return 0;
		}
		
		long l1=calculateTf();
		long l2= o.calculateTf();
		//return l1<l2 ? 1 : (l1==l2 ? 0:-1);
          
        if(l1 < l2 )
			return 1;
		else if(l1 > l2)
			return -1;
		else{ 
			
			if(titleCount < o.getTitleCount())
				return 1;
			else if(titleCount > o.getTitleCount())
				return -1;
			else {
				if(textCount < o.getTextCount())
					return 1;
				else if(textCount > o.getTextCount())
					return -1;
				else {
					if((infoBoxCount+outlinkCount+catagoryCount) < (o.getInfoBoxCount()+o.getOutlinkCount()+o.getCatagoryCount()))
						return 1;
					else if((infoBoxCount+outlinkCount+catagoryCount) > (o.getInfoBoxCount()+o.getOutlinkCount()+o.getCatagoryCount()))
						return -1;
					else
						return 0;
				}
			}
                        
		}
		
	}

	public void mergePosting(Posting p) {
		if(p == null)
			return;
		
		setTitleCount(getTitleCount()+p.getTitleCount());
		setTextCount(getTextCount()+p.getTextCount());
		setInfoBoxCount(getInfoBoxCount()+p.getInfoBoxCount());
		setOutlinkCount(getOutlinkCount()+p.getOutlinkCount());
		setCatagoryCount(getCatagoryCount()+p.getCatagoryCount());
		
	}
	public long getTitleCount() {
		return titleCount;
	}
	public void setTitleCount(long titleCount) {
		this.titleCount = titleCount;
	}
	public long getTextCount() {
		return textCount;
	}
	public void setTextCount(long textCount) {
		this.textCount = textCount;
	}
	public long getInfoBoxCount() {
		return infoBoxCount;
	}
	public void setInfoBoxCount(long infoBoxCount) {
		this.infoBoxCount = infoBoxCount;
	}
	public long getOutlinkCount() {
		return outlinkCount;
	}
	public void setOutlinkCount(long outlinkCount) {
		this.outlinkCount = outlinkCount;
	}
	public long getCatagoryCount() {
		return catagoryCount;
	}
	public void setCatagoryCount(long catagoryCount) {
		this.catagoryCount = catagoryCount;
	}

	public String getMeta() {
		return meta;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
}
