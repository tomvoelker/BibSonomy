/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jabref.export.layout.format;

import static org.bibsonomy.util.ValidationUtils.present;

import net.sf.jabref.GlobalsSuper;
import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * The difference between this class and {@link HTMLChars} is the implemented
 * AntiScriptInjection, the 'field' replaceAll()-statements and 'final' access
 * modifiers. This formatter escapes characters so they are suitable for HTML.
 * 
 * @author JabRef, vhems, dzo
 */
public class HTMLCharsAntiScript implements LayoutFormatter {
	private static final String HTML_AMP = "&amp;";
	private static final char NEW_LINE = '\n';

	@Override
	public String format(String field) {
		int i;
		final StringBuilder sb = new StringBuilder();
		StringBuilder currentCommand = null;

		char c;
		boolean escaped = false, incommand = false;

		for (i = 0; i < field.length(); i++) {
			c = field.charAt(i);
			if (c == '&') {
				sb.append(HTML_AMP);
				if (incommand && ((i == field.length() - 1) || Character.isWhitespace(field.charAt(i + 1)))) {
					incommand = false;
				}
			} else if (c == NEW_LINE) {
				final int nextCharIndex = i + 1;
				boolean beginPara = false;
				if (nextCharIndex < field.length()) {
					beginPara = field.charAt(nextCharIndex) == NEW_LINE;
					i = nextCharIndex + 1;
				}
				if (beginPara) {
					sb.append("<p>");
				} else {
					sb.append("<br>");
				}
			} else if (escaped && (c == '\\')) {
				sb.append('\\');
				escaped = false;
			} else if (c == '\\') {
				if (incommand) {
					/* Close Command */
					final String command = currentCommand.toString();
					sb.append(convertToHTML(command));
				}
				escaped = true;
				incommand = true;
				currentCommand = new StringBuilder();
			} else if (!incommand && (c == '{' || c == '}')) {
				// Swallow the brace.
			} else if (Character.isLetter(c) || (c == '%') || (GlobalsSuper.SPECIAL_COMMAND_CHARS.indexOf(String.valueOf(c)) >= 0)) {
				escaped = false;

				if (!incommand) {
					sb.append(formatAntiScriptInjection(c));
					// Else we are in a command, and should not keep the letter.
				} else {
					currentCommand.append(c);
					testCharCom: if ((currentCommand.length() == 1) && (GlobalsSuper.SPECIAL_COMMAND_CHARS.indexOf(currentCommand.toString()) >= 0)) {
						// This indicates that we are in a command of the type
						// \^o or \~{n}
						if (i >= field.length() - 1) break testCharCom;

						final String command = currentCommand.toString();
						i++;
						c = field.charAt(i);
						String combody;
						if (c == '{') {
							final IntAndString part = getPart(field, i, false);
							i += part.i;
							combody = part.s;
						} else {
							combody = field.substring(i, i + 1);
						}
						
						sb.append(convertToHTML(command + combody, ""));
						
						incommand = false;
						escaped = false;
					} else {
						// Are we already at the end of the string?
						if (i + 1 == field.length()) {
							final String command = currentCommand.toString();
							sb.append(convertToHTML(command));
						}
					}
				}
			} else {
				String argument = null;
				if (!incommand) {
					sb.append(formatAntiScriptInjection(c));
				} else if (Character.isWhitespace(c) || (c == '{') || (c == '}')) {
					// First test if we are already at the end of the string.
					// if (i >= field.length() - 1)
					// break testContent;

					final String command = currentCommand.toString();

					// Then test if we are dealing with a italics or bold
					// command.
					// If so, handle.
					if (command.equals("em") || command.equals("emph") || command.equals("textit")) {
						final IntAndString part = getPart(field, i, true);

						i += part.i;
						sb.append("<em>").append(antiScriptInjection(part.s)).append("</em>");
					} else if (command.equals("textbf")) {
						final IntAndString part = getPart(field, i, true);
						i += part.i;
						sb.append("<b>").append(antiScriptInjection(part.s)).append("</b>");
					} else if (c == '{') {
						final IntAndString part = getPart(field, i, true);
						i += part.i;
						argument = part.s;
						if (argument != null) {
							// handle common case of general latex command
							sb.append(convertToHTML(command + argument, argument));
						}
					} else if (c == '}') {
						// This end brace terminates a command. This can be the
						// case in
						// constructs like {\aa}. The correct behaviour should
						// be to
						// substitute the evaluated command and swallow the
						// brace:
						sb.append(convertToHTML(command));
					} else {
						sb.append(convertToHTML(command));
					}
				}/*
				 * else if (c == '}') {
				 * System.out.printf("com term by }: '%s'\n",
				 * currentCommand.toString());
				 * 
				 * argument = ""; }
				 */else {
					/*
					 * TODO: this point is reached, apparently, if a command is
					 * terminated in a strange way, such as with "$\omega$".
					 * Also, the command "\&" causes us to get here. The former
					 * issue is maybe a little difficult to address, since it
					 * involves the LaTeX math mode. We don't have a complete
					 * LaTeX parser, so maybe it's better to ignore these
					 * commands?
					 */
				}

				incommand = false;
				escaped = false;
			}
		}

		return sb.toString();
	}

	private static String convertToHTML(final String command) {
		return convertToHTML(command, command);
	}
	
	/**
	 * @param command
	 * @param fallback
	 */
	private static String convertToHTML(final String command, final String fallback) {
		final String result = GlobalsSuper.HTMLCHARS.get(command);
		if (result != null) {
			return result;
		}
		// If the command is unknown, just print the fallback:
		return antiScriptInjection(fallback);
	}

	/**
	 * @param string
	 * @return
	 */
	private static String antiScriptInjection(final String string) {
		if (!present(string)) {
			return string;
		}
		final StringBuilder builder = new StringBuilder();
		for (char ch : string.toCharArray()) {
			builder.append(formatAntiScriptInjection(ch));
		}
		return builder.toString();
	}

	private static String formatAntiScriptInjection(char c) {
		switch (c) {
		case '<':
			return "&lt;";
		case '>':
			return "&gt;";
		default:
			return Character.toString(c);
		}
	}

	private IntAndString getPart(final String text, int i, final boolean terminateOnEndBraceOnly) {
		char c;
		int count = 0;

		final StringBuilder part = new StringBuilder();

		// advance to first char and skip wihitespace
		i++;
		while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
			i++;
		}

		// then grab whathever is the first token (counting braces)
		while (i < text.length()) {
			c = text.charAt(i);
			if (!terminateOnEndBraceOnly && count == 0 && Character.isWhitespace(c)) {
				i--; // end argument and leave whitespace for further
						// processing
				break;
			}
			if (c == '}' && --count < 0)
				break;
			else if (c == '{') count++;
			part.append(c);
			i++;
		}
		return new IntAndString(part.length(), format(part.toString()));
	}

	private class IntAndString {
		public int i;

		String s;

		public IntAndString(final int i, final String s) {
			this.i = i;
			this.s = s;
		}
	}
}
