package org.bibsonomy.rest.renderer.impl.xml;

import java.io.IOException;
import java.io.Writer;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.bind.marshaller.MinimumEscapeHandler;

/**
 * A {@link CharacterEscapeHandler} like {@link MinimumEscapeHandler} that
 * also replaces new lines with <code>&#10;</code>
 * 
 * XXX: constructor of {@link MinimumEscapeHandler} is private so this class
 * is a code copy of it
 * @author dzo
 */
public class NewLineEscapeHandler implements CharacterEscapeHandler {
	/** the instance */
	public static final CharacterEscapeHandler theInstance = new NewLineEscapeHandler();
	
	private NewLineEscapeHandler() {
		 // no instanciation please
	}

	@Override
	public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
		// avoid calling the Writerwrite method too much by assuming
		// that the escaping occurs rarely.
		// profiling revealed that this is faster than the naive code.
		int limit = start + length;
		for (int i = start; i < limit; i++) {
			char c = ch[i];
			if (c == '&' || c == '<' || c == '>' || c == '\r'
					|| ((c == '\"' || c == '\n') && isAttVal)) {
				if (i != start)
					out.write(ch, start, i - start);
				start = i + 1;
				switch (ch[i]) {
				case '&':
					out.write("&amp;");
					break;
				case '<':
					out.write("&lt;");
					break;
				case '>':
					out.write("&gt;");
					break;
				case '\"':
					out.write("&quot;");
					break;
				case '\n':
					out.write("&#10;");
					break;
				}
			}
		}

		if (start != limit)
			out.write(ch, start, limit - start);
	}
}
