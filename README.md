WikiSearch
==========

[![Build Status](https://travis-ci.org/dharmeshkakadia/WikiSearch.svg)](https://travis-ci.org/dharmeshkakadia/WikiSearch)

Wikipedia Search engine

##Build 

WikiSearch can be build using maven.

##Use

Can be used in batch and interactive query mode.

For single Query with indexes alrady build : 

java -jar WikiSearch.jar InputFile OutputFile SecondaryIndexPath MainIndexPath ForwardTitleIndexPath SparseIndexCounter(Default:5) SearchTerms(with meta)

Where meta is indicator of what all to consider while searching 

* t - title
* c - content or text
* i - infobox
* o - outlink
* g - category

Example : java -jar WikiSearch.jar InputFile OutputFile SecondaryIndexPath MainIndexPath ForwardTitleIndexPath 5 "t: India c: Politics"

ToDo :

+ Add batch mode Query example
+ Add index creation example in Documentation




