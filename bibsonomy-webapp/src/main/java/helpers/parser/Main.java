package helpers.parser;

import java.io.StringReader;
import resources.Tag;

public class Main {
    public static void main(String[] args) throws Exception {
    	//String       s = "taga tagb äöü tagc tagd->tage<-tagh <-foo<-bar bar<asd bar>asd foo-bar-blubb"; // Fehler: null<-foo als Relation
    	String       s = "<-foo<-bar"; // Fehler: null<-foo als Relation
    	//String s = "zsh->shell->unix tricks";
    	StringReader r = new StringReader (s);
    	//ExprLexer    l = new ExprLexer(System.in);
    	Tag               t = new Tag();
    	TagStringLexer    l = new TagStringLexer(r);
    	TagStringParser   p = new TagStringParser(l, t);
    	p.tagstring();
    	
    	System.out.println(s);
    	//System.out.println(liste);
    	
    	System.out.println(t.getTags());
    }
}