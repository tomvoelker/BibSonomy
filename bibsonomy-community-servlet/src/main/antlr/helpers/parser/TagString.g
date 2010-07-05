/******************************************************************
* 
* This grammar is for ANTLR 3.0.1
* When using ANTLR 3.0.1 you have to edit the pom.xml
* 1) classname="org.antlr.Tool"
* 2) line="-o ${project.build.directory}/generated-sources/antlr/"
* 3) value="helpers/parser/TagString.g3"
* 4) Change dependency values:
* 4.1) groupID = org.antlr
* 4.2) version = 3.0b5 (3.0.1 doesn't work)
* 5) dependency for antlr runtime: its already included in JabRef-2.3b.jar
* 
********************************************************************/


grammar TagString;

options {
	k=2;
	//output=AST; // verursacht "komische" Methoden (mit Rueckgabewert) in der ParserKlasse 
	ASTLabelType=CommonTree;
} 

tokens {
	LEFTARROW = '<-';
	RIGHTARROW = '->';
}


@header {
	package helpers.parser;
	import resources.Tag;
}
@lexer::header {
	package helpers.parser;
}

@members{
	String lasttag = null;
	Tag tagobj;
        
	// constructor with Tag object included
	public TagStringParser(TokenStream lexer, Tag t) {
		//	this(lexer,1); // does not work with ANTLR3 ... what did the 1 mean?
		this(lexer); // neccessary, because no parameter-less constructor given
		this.tagobj = t;
	}
	
	// for own exception catching
//	@Override
//	protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException {
//		throw new MismatchedTokenException(ttype, input);
//  }
    
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
    catch (Exception e) {
    	// getTokenErrorDisplay() does only work with ANTLR 3.0.1, alternative: getText()
    	//String err = "line: "+e.line+", "+e.charPositionInLine+"; found "+getTokenErrorDisplay(e.token)+
    	//String err = "line: "+e.line+", "+e.charPositionInLine+"; found '"+e.token.getText()+
    	//			 "' but expected "+tokenNames[e.expecting];
    	//System.out.println(err);
    }
}

//		ctag (SPACE ctag)*
tagstring
	:    ctag ( SPACE ctag )* 
    ;

//		tag ( uprel | lorel )*
ctag
	@init{
		if (tagobj.tagCount() > Tag.MAX_TAGS_ALLOWED) return;
	}
    :   t = tag {lasttag=t;} (uprel | lorel)*  
    ;
 
//		<- tag
uprel 
    :    LEFTARROW t = tag  {tagobj.addTagRelation(t, lasttag); lasttag=t;}
    ;


//		-> tag
lorel
    :    RIGHTARROW  t = tag {tagobj.addTagRelation(lasttag, t); lasttag=t;}
    ;

norel
    :    LEFTARROW | RIGHTARROW
    ;
    

//		TAG
tag returns [String t = null]
	@init{
       // System.out.println("found " + t);
	}
    :   tt=TAG {t = tt.getText(); tagobj.addTag(t);}
    ;



/******************************************************************
* Lexer Code Starts Here
********************************************************************/
   
SPACE	:	( ' ' | '\t' )+;
//SPACE	:	( ' ' | '\t' | ' <-' | ' ->' | '-> ' | '<- ' )+;

// skip line breaks
WS		:	( '\r' | '\n' ) {$channel=HIDDEN;};
//RELWITHSPACE	:	( ' <-' | ' ->' | '-> ' | '<- ' ) {$channel=HIDDEN;};

// a tag is every character, but ...

TAG
	: (	~ ( '\n' | '\r' | '\t' | ' '| '-' | '<' ) 
	|	SPECIAL1
	|   SPECIAL2
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


