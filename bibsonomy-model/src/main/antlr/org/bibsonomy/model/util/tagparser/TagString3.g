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
	import org.antlr.runtime.BitSet;
  import org.antlr.runtime.CommonTokenStream;
  import org.antlr.runtime.IntStream;
  import org.antlr.runtime.MismatchedSetException;
  import org.antlr.runtime.MismatchedTokenException;
  import org.antlr.runtime.Parser;
  import org.antlr.runtime.RecognitionException;
  import org.antlr.runtime.Token;
  import org.antlr.runtime.TokenStream;
	
	import org.bibsonomy.model.Tag;
	import java.util.Set;
	import java.util.HashMap;
}
@lexer::header {
	package org.bibsonomy.model.util.tagparser;
}

@members{
	Tag lastTag = null;
	Set<Tag> tags;
	HashMap<String, Tag> tagList = new HashMap<String, Tag>();
        
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
	protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException {
		throw new MismatchedTokenException(ttype, input);
  }
}
// catchblock for all methods in the parser
// print what the parser found and what it expected
@rulecatch {
    catch (MismatchedTokenException e) {
    	String err = "line: "+e.line+", "+e.charPositionInLine+"; found "+getTokenErrorDisplay(e.token)+ " but expected "+tokenNames[e.expecting];
//    	System.out.println(err);
    }
}

//ctag (SPACE ctag)*
tagstring
	:    ctag ( SPACE ctag )* 
	;

//tag ( uprel | lorel )*
ctag
	@init{
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
            //System.out.println("found |" + tt.getText() + "|");
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


