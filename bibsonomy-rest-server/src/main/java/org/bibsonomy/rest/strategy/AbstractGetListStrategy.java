package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.xml.tools.EscapingPrintWriter;

/**
 * @author Jens Illig
 * @version $Id$
 * @param <L> 
 */
public abstract class AbstractGetListStrategy<L extends List<?>> extends Strategy {
	private final ViewModel view;
	protected Writer writer;
	
	/**
	 * @param context
	 */
	public AbstractGetListStrategy(final Context context) {
		super(context);
		this.view = new ViewModel();
		this.view.setStartValue( context.getIntAttribute("start", 0) );
		this.view.setEndValue( context.getIntAttribute("end", 20) );
		this.view.setOrder( context.getStringAttribute("order", null) );
		if (view.getStartValue() > view.getEndValue()) {
			throw new BadRequestOrResponseException("start must be less than or equal end");
		}
	}

	@Override
	public final void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		writer = new EscapingPrintWriter(outStream);
		final L resultList = getList();
		
		if (resultList.size() != (getView().getEndValue() - getView().getStartValue())) {
			this.view.setEndValue( resultList.size() + this.view.getStartValue());
		} else {
			this.view.setUrlToNextResources( buildNextLink() );
		}
		render(writer, resultList);
	}

	protected abstract void render(Writer writer, L resultList);

	protected abstract L getList();

	private final String buildNextLink() {
		final StringBuilder sb = getLinkPrefix();
		sb.append("?start=").append(getView().getEndValue()).append("&end=").append(getView().getEndValue() + getView().getEndValue() - getView().getStartValue());
		appendLinkPostFix(sb);
		return sb.toString();
	}

	protected abstract StringBuilder getLinkPrefix();

	protected abstract void appendLinkPostFix(StringBuilder sb);

	protected ViewModel getView() {
		return this.view;
	}
}