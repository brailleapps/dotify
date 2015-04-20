package org.daisy.dotify.engine.impl;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.daisy.dotify.api.engine.FormatterEngineConfigurationException;
import org.daisy.dotify.api.engine.FormatterEngineFactoryService;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.MarkerProcessorFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriter;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class LayoutEngineFactoryImpl implements FormatterEngineFactoryService {
	private FormatterFactory ff;
	private MarkerProcessorFactoryMakerService mpf;
	private TextBorderFactoryMakerService tbf;
	private ExpressionFactory ef;


	public LayoutEngineImpl newFormatterEngine(String locale, String mode, PagedMediaWriter writer) {
		XMLInputFactory in = XMLInputFactory.newInstance();
		in.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		in.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		in.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		in.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		XMLEventFactory xef = XMLEventFactory.newInstance();
		XMLOutputFactory of = XMLOutputFactory.newInstance();
		return new LayoutEngineImpl(locale, mode, writer, ff, mpf, tbf, ef, in, xef, of);
	}

	public <T> void setReference(Class<T> c, T factory) throws FormatterEngineConfigurationException {
		if (c.equals(FormatterFactory.class)) {
			setFormatterFactory((FormatterFactory)factory);
		} else if (c.equals(MarkerProcessorFactoryMakerService.class)) {
			setMarkerProcessor((MarkerProcessorFactoryMakerService)factory);
		} else if (c.equals(TextBorderFactoryMakerService.class)) {
			setTextBorderFactoryMaker((TextBorderFactoryMakerService)factory);
		} else if (c.equals(ExpressionFactory.class)) {
			setExpressionFactory((ExpressionFactory)factory);
		}
		
		else {
			throw new FormatterEngineConfigurationException("Unrecognized reference: " +factory);
		}
	}
	
	// FIXME: not a service
	@Reference
	public void setFormatterFactory(FormatterFactory formatterFactory) {
		this.ff = formatterFactory;
	}

	public void unsetFormatterFactory(FormatterFactory formatterFactory) {
		this.ff = null;
	}

	@Reference
	public void setMarkerProcessor(MarkerProcessorFactoryMakerService mp) {
		this.mpf = mp;
	}

	public void unsetMarkerProcessor(MarkerProcessorFactoryMakerService mp) {
		this.mpf = null;
	}

	@Reference
	public void setTextBorderFactoryMaker(TextBorderFactoryMakerService tbf) {
		this.tbf = tbf;
	}

	public void unsetTextBorderFactoryMaker(TextBorderFactoryMakerService tbf) {
		this.tbf = null;
	}

	@Reference
	public void setExpressionFactory(ExpressionFactory ef) {
		this.ef = ef;
	}

	public void unsetExpressionFactory(ExpressionFactory ef) {
		this.ef = null;
	}

}
