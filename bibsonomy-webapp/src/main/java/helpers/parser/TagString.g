header {
	package helpers.parser;
}

{
	import resources.Tag;
}

class TagStringParser extends Parser;

{
	String lasttag = null;
	Tag tagobj;
	
	// constructor with Tag object included
	public TagStringParser(TokenStream lexer, Tag t) {
      this(lexer,1);
      this.tagobj = t;
    }
	
}

//  ctag (SPACE ctag)*
tagstring
    :    ctag (SPACE ctag)* 
    ;

//	tag (uprel|lorel)*
ctag
{
	String t;
	if (tagobj.tagCount() > Tag.MAX_TAGS_ALLOWED) return;
}
    :    t = tag {lasttag=t;} (uprel | lorel)*  
    ;
 
//	<- tag
uprel 
{
	String t;
}
    :    LEFTARROW t = tag  {tagobj.addTagRelation(t, lasttag); tagobj.addTag(t); tagobj.addTag(lasttag); lasttag=t;}
    ;

// 	-> tag
lorel
{
	String t;
}
    :    RIGHTARROW t = tag {tagobj.addTagRelation(lasttag, t); tagobj.addTag(t); tagobj.addTag(lasttag); lasttag=t;}
    ;

// 	TAG
tag returns [String t = null]
    :    tt:TAG {t = tt.getText(); tagobj.addTag(t);}
    ;
    exception
    catch [MismatchedTokenException e] {
	   // System.out.println(e.getClass() +  ": " + e + ", t = " + t);
    }



class TagStringLexer extends Lexer;

options {
    k=2; // needed for newline junk
    charVocabulary='\u0000'..'\uFFFE'; 
    defaultErrorHandler=false ;
}
   
SPACE      : (' '|'\t')+;
LEFTARROW  : "<-";
RIGHTARROW : "->";
TAG        : (
              ~(
                 '\n'
                |'\r'
                |'\t'
                |' '
                |'-'
                |'<'
               )
              |{LA(2)!='>'}? '-'
              |{LA(2)!='-'}? '<'
             )+; 
WS         : ( '\r' '\n' | '\n' ) {$setType(Token.SKIP);};
