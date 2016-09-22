package org.bibsonomy.webapp.util.markdown;

import java.util.List;
import java.util.Map;

import org.pegdown.LinkRenderer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.SuperNode;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

/**
 * TODO: add documentation to this class
 *
 * @author nosebrain
 */
public class ToHtmlSerializer extends org.pegdown.ToHtmlSerializer {

	/**
	 * @param linkRenderer
	 * @param plugins
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer, List<ToHtmlSerializerPlugin> plugins) {
		super(linkRenderer, plugins);
	}

	/**
	 * @param linkRenderer
	 * @param verbatimSerializers
	 * @param plugins
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers, List<ToHtmlSerializerPlugin> plugins) {
		super(linkRenderer, verbatimSerializers, plugins);
	}

	/**
	 * @param linkRenderer
	 * @param verbatimSerializers
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers) {
		super(linkRenderer, verbatimSerializers);
	}

	/**
	 * @param linkRenderer
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer) {
		super(linkRenderer);
	}

	/* (non-Javadoc)
	 * @see org.pegdown.ToHtmlSerializer#printIndentedTag(org.pegdown.ast.SuperNode, java.lang.String)
	 */
	@Override
	protected void printIndentedTag(SuperNode node, String tag) {
		// XXX: not so nice, but we want to add a class attribute to the table tag
		if ("table".equals(tag)) {
			printer.println().print("<table class=\"table\">").indent(+2);
			visitChildren(node);
			printer.indent(-2).println().print('<').print('/').print(tag).print('>');
		} else {
			super.printIndentedTag(node, tag);
		}
	}
}
