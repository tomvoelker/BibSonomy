/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.markdown;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringBuilderVar;
import org.pegdown.Parser;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.Node;
import org.pegdown.plugins.BlockPluginParser;
import org.pegdown.plugins.InlinePluginParser;
import org.pegdown.plugins.PegDownPlugins;

/**
 * the plugin extension used by the help pages
 *
 * @author Johannes Blum
 */
public class Plugin extends Parser implements InlinePluginParser, BlockPluginParser {
	
	/**
	 * Instantiates a new Parser.
	 */
	public Plugin() {
		super(ALL, 1000l, DefaultParseRunnerProvider);
	}

	/* (non-Javadoc)
	 * @see org.pegdown.plugins.InlinePluginParser#inlinePluginRules()
	 */
	@Override
	public Rule[] inlinePluginRules() {
		return new Rule[] {Variable()};
	}
	
	/* (non-Javadoc)
	 * @see org.pegdown.plugins.BlockPluginParser#blockPluginRules()
	 */
	@Override
	public Rule[] blockPluginRules() {
		return new Rule[] {CondExpression()};
	}
	
	/**
	 * Rule for a Variable.
	 */
	public Rule Variable() {
		StringBuilderVar varName = new StringBuilderVar();
		
		return Sequence(
			"${",
			ZeroOrMore(TestNot('}'), BaseParser.ANY, varName.append(matchedChar())),
			"}",
			TestNot('{'),
			push(new VariableNode(varName.getString()))
		);
	}
	
	/**
	 * Rule for a ConditionalExpression.
	 * @return 
	 */
	public Rule CondExpression() {
		StringBuilderVar condition = new StringBuilderVar();
		StringBuilderVar body = new StringBuilderVar();
		
		
		return Sequence(
				"${",
				ZeroOrMore(TestNot('}'), BaseParser.ANY, condition.append(matchedChar())),
				"}{",
				ZeroOrMore(FirstOf(
					Sequence(Variable(), body.append(pop().toString())),
					Sequence(CondExpression(), body.append(((ConditionalNode) pop()).getSource())),
					Sequence(TestNot('}'), BaseParser.ANY, body.append(matchedChar()))
				)),
				"}",
				push(new ConditionalNode(condition.getString(), parse(body), body.getString()))
			);
	}
	
	/**
	 * Parse the content of body with a new PegDownProcessor.
	 * 
	 * This is required as pegdown does not privide enought visibility to use
	 * its existing parsing rules directly.
	 * @param body a StringBuilderVar containing the string to parse
	 * @return the parsed Sting as a list of nodes
	 */
	public List<Node> parse(StringBuilderVar body) {
		final PegDownProcessor processor = new PegDownProcessor(org.bibsonomy.webapp.util.markdown.Parser.PROCESSOR_CONFIG, new PegDownPlugins.Builder().withPlugin(Plugin.class).build());
		return processor.parseMarkdown(body.getChars()).getChildren();
	}

}
