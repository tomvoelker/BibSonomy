// $ANTLR : "TagString.g" -> "TagStringParser.java"$

package helpers.parser;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

import resources.Tag;

public class TagStringParser extends antlr.LLkParser implements
		TagStringParserTokenTypes {

	String lasttag = null;

	Tag tagobj;

	// constructor with Tag object included
	public TagStringParser(TokenStream lexer, Tag t) {
		this(lexer, 1);
		this.tagobj = t;
	}

	protected TagStringParser(TokenBuffer tokenBuf, int k) {
		super(tokenBuf, k);
		tokenNames = _tokenNames;
	}

	public TagStringParser(TokenBuffer tokenBuf) {
		this(tokenBuf, 1);
	}

	protected TagStringParser(TokenStream lexer, int k) {
		super(lexer, k);
		tokenNames = _tokenNames;
	}

	public TagStringParser(TokenStream lexer) {
		this(lexer, 1);
	}

	public TagStringParser(ParserSharedInputState state) {
		super(state, 1);
		tokenNames = _tokenNames;
	}

	public final void tagstring() throws RecognitionException,
			TokenStreamException {

		try { // for error handling
			ctag();
			{
				_loop3: do {
					if ((LA(1) == SPACE)) {
						match(SPACE);
						ctag();
					} else {
						break _loop3;
					}

				} while (true);
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			recover(ex, _tokenSet_0);
		}
	}

	public final void ctag() throws RecognitionException, TokenStreamException {

		String t;
		if (tagobj.tagCount() > Tag.MAX_TAGS_ALLOWED)
			return;

		try { // for error handling
			t = tag();
			lasttag = t;
			{
				_loop6: do {
					switch (LA(1)) {
					case LEFTARROW: {
						uprel();
						break;
					}
					case RIGHTARROW: {
						lorel();
						break;
					}
					default: {
						break _loop6;
					}
					}
				} while (true);
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			recover(ex, _tokenSet_1);
		}
	}

	public final String tag() throws RecognitionException, TokenStreamException {
		String t = null;

		Token tt = null;

		try { // for error handling
			tt = LT(1);
			match(TAG);
			t = tt.getText();
			tagobj.addTag(t);
		} catch (MismatchedTokenException e) {

			// System.out.println(e.getClass() + ": " + e + ", t = " + t);

		}
		return t;
	}

	public final void uprel() throws RecognitionException, TokenStreamException {

		String t;

		try { // for error handling
			match(LEFTARROW);
			t = tag();
			tagobj.addTagRelation(t, lasttag);
			tagobj.addTag(t);
			tagobj.addTag(lasttag);
			lasttag = t;
		} catch (RecognitionException ex) {
			reportError(ex);
			recover(ex, _tokenSet_2);
		}
	}

	public final void lorel() throws RecognitionException, TokenStreamException {

		String t;

		try { // for error handling
			match(RIGHTARROW);
			t = tag();
			tagobj.addTagRelation(lasttag, t);
			tagobj.addTag(t);
			tagobj.addTag(lasttag);
			lasttag = t;
		} catch (RecognitionException ex) {
			reportError(ex);
			recover(ex, _tokenSet_2);
		}
	}

	public static final String[] _tokenNames = { "<0>", "EOF", "<2>",
			"NULL_TREE_LOOKAHEAD", "SPACE", "LEFTARROW", "RIGHTARROW", "TAG",
			"WS" };

	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

	private static final long[] mk_tokenSet_1() {
		long[] data = { 18L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());

	private static final long[] mk_tokenSet_2() {
		long[] data = { 114L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());

}
