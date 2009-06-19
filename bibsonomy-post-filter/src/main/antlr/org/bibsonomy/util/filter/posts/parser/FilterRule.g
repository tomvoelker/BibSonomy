grammar FilterRule;

options {
	k=2;
	ASTLabelType=CommonTree;
} 

tokens {
  // matchers
  OR     = '|';
  AND    = '&';

  LPAREN = '(';
  RPAREN = ')';
  LBRACK = '[';
  RBRACK = ']';
  
  SEMI   = ';';
  APO    = '\'';
  
  DO     = ':';
  
  // comparators
  EQUALS = '=';
  LT     = '<';
  LE     = '<=';
  GT     = '>';
  GE     = '>=';
  MATCH  = '=~';
  
  
  // unary operators
  NOT    = '!';
}


@header {
  package org.bibsonomy.util.filter.posts.parser;
  import org.antlr.runtime.BitSet;
  import org.antlr.runtime.CommonTokenStream;
  import org.antlr.runtime.IntStream;
  import org.antlr.runtime.MismatchedSetException;
  import org.antlr.runtime.MismatchedTokenException;
  import org.antlr.runtime.Parser;
  import org.antlr.runtime.RecognitionException;
  import org.antlr.runtime.Token;
  import org.antlr.runtime.TokenStream;
	
  import org.bibsonomy.util.filter.posts.*;
  import org.bibsonomy.util.filter.posts.comparator.*;
  import org.bibsonomy.util.filter.posts.matcher.*;
  import org.bibsonomy.util.filter.posts.modifier.*;
}

@lexer::header {
	package org.bibsonomy.util.filter.posts.parser;
}

@members{
    private final PostFilter postFilter = new PostFilter();
    
    public PostFilter getPostFilter() {
        return postFilter;
    }
    
// 	@Override
//	protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException {
//		throw new MismatchedTokenException(ttype, input);
//  }
}
// catchblock for all methods in the parser
// print what the parser found and what it expected
@rulecatch {
    catch (MismatchedTokenException e) {
    	String err = "line: "+e.line+", "+e.charPositionInLine+"; found "+getTokenErrorDisplay(e.token)+ " but expected "+tokenNames[e.expecting];
    	System.err.println(err);
    	e.printStackTrace();
    }
}

filter_with_eof
	: filter EOF
	;
  
filter
    : ma = expr {postFilter.setMatcher(ma);} (DO mo = modifier {postFilter.setModifier(mo);})?
	;

/* ****************************************************************************
 * matcher part 
 * ****************************************************************************/

expr returns [Matcher m]
	: rm = simpleExpression {m = rm;}
	| LPAREN cm = conditionalExpression RPAREN {m = cm;}
	| am = all LBRACK (em = expr {am.addMatcher(em);})+ RBRACK {m = am;}
	;
	
conditionalExpression returns [BinaryMatcher m]
	: r1 = expr mm = condition r2 = expr
	{
	  m = mm;
	  m.setLeft(r1);
	  m.setRight(r2);
	}
	;

simpleExpression returns [Matcher m]
 	: rm = relationalExpression {m = rm;}
 	| um = unaryExpression {m = um;}
 	;

unaryExpression returns [Matcher m]
 	: NOT nm = expr {m = new BooleanNotMatcher(nm);}
 	;

relationalExpression returns [Matcher m]
	: i = identifier r = relation v = value      {m = new BeanPropertyMatcher(i, r, v);}
	| v = value      r = relation i = identifier {m = new BeanPropertyMatcher(i, r, v);}
	;  

identifier returns [String s]
	: id = ID { s = id.getText(); }
	;

value returns [String s]
	: id = STRINGLITERAL { String ss = id.getText(); s = ss.substring(1,ss.length() - 1);}
	| n = NUMBER         { s =  n.getText(); }
	;

relation returns [Comparator c]
	: EQUALS {c = new Equals();} 
	| LT     {c = new LessThan();}
	| LE     {c = new LessOrEqual();}
	| GT     {c = new GreaterThan();}
	| GE     {c = new GreaterOrEqual();}
	| MATCH  {c = new Matches();}
	;
	
condition returns [BinaryMatcher m]
	: OR     {m = new BooleanOrMatcher();}
	| AND    {m = new BooleanAndMatcher();}
	;
	
all returns [AllMatcher m]
	: OR   {m = new BooleanAllOrMatcher();}
	| AND  {m = new BooleanAllAndMatcher();}
	;

/* ****************************************************************************
 * modifier part 
 * ****************************************************************************/
modifier returns [Modifier m]
	: i = identifier ':=' v = value {m = new PropertyModifier(i, v);} 
	;



/******************************************************************
 * Lexer Code Starts Here
 ********************************************************************/

// ignore whitespace
WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ 	{ $channel = HIDDEN; } ;

// identifiers
ID : ('a'..'z'|'A'..'Z'|'_'|'.') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.')* ;

STRINGLITERAL 
    :   '\''
        ( ~( '\'' | '"' | '\r' | '\n' ) )*
        '\''
    ;

// numbers
NUMBER : ('0'..'9')+;
