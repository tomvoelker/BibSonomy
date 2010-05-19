grammar TagString3;

options {
	k=2;
	ASTLabelType=CommonTree;
} 

tokens {
	LEFTARROW = '<-';
	RIGHTARROW = '->';
}


@header {

package org.bibsonomy.model.util.tagparser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Tag;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
}

@lexer::header {

package org.bibsonomy.model.util.tagparser;

}

@members{

	private static final Log log = LogFactory.getLog(TagString3Parser.class);

	private Tag lastTag = null;
	private Set<Tag> tags;
	private Map<String, Tag> tagList = new HashMap<String, Tag>();
        
	/**
	 * constructor with Tag object included
	 * @param tokens
	 * @param tags
	 */
	public TagString3Parser(CommonTokenStream tokens, Set<Tag> tags) {
		this(tokens); 
		this.tags = tags;
	}

	@Override
	public boolean mismatchIsMissingToken(IntStream input, BitSet follow) {
		return false;
	}
  
	@Override
	public boolean mismatchIsUnwantedToken(IntStream input, int ttype) {
		return false;
	}

}

// catchblock for all methods in the parser
// print what the parser found and what it expected
@rulecatch {
    catch (final Exception e) {
    	log.fatal("parser exception: ", e);
    }
}

//ctag (SPACE ctag)*
tagstring
	:    ctag ( SPACE ctag )* 
	;

//tag ( uprel | lorel )*
ctag
	@init {
		if (tags.size() >= Tag.MAX_TAGS_ALLOWED) return;
	}
    :   t = tag {lastTag=t;} (uprel | lorel)* 
    ;
 
//<- tag (super<-sub)
//tags.addTagRelation(t, lastTag);
uprel 
    :   LEFTARROW t = tag  
		    {
          if (lastTag == null || t == null) return;
            lastTag.addSubTag(t);
            t.addSuperTag(lastTag);
            lastTag=t;
		    }
    ;


//-> tag (sub->super)
//tags.addTagRelation(lastTag, t);
lorel
    :   RIGHTARROW  t = tag 
        {
         if (lastTag == null || t == null) return;
           t.addSubTag(lastTag);
           lastTag.addSuperTag(t);
			     lastTag=t;
		    }
    ;

norel
    :    LEFTARROW | RIGHTARROW;
    

//		TAG
tag returns [Tag t = null]
    :   tt=TAG {
            log.debug("found |" + tt.getText() + "|");
						if (!tagList.containsKey(tt.getText())) {
							t = new Tag(tt.getText());
							                      
							tags.add(t);
							tagList.put(tt.getText(), t);
						} else {
						  t = tagList.get(tt.getText());
						}
        }
    ;



/******************************************************************
 * Lexer Code Starts Here
 ********************************************************************/
   
SPACE	:	( ' ' | '\t' )+;

// skip line breaks
WS		:	( '\r' | '\n' ) {$channel=HIDDEN;};

// a tag is every character, but ...
TAG
	: (	~( '\n' | '\r' | '\t' | ' '| '-' | '<' ) 
	|	SPECIAL1
	| SPECIAL2
	  )+
	;
	


	
/**
* In following Token you must use the gated semantic predicate '?=>'
* without this gated semantic predicate it doesn't work.
* It's not obvious to use this, because you doesn't find anything about
* gated semantic predicate predicate in the Antlr Migration FAQ 
*/
SPECIAL1
	: {input.LA(2)!='>'}?=> '-' 
	;

SPECIAL2
	: {input.LA(2)!='-'}?=> '<'
	;


