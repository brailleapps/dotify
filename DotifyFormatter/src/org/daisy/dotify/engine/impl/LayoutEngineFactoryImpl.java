package org.daisy.dotify.engine.impl;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.daisy.dotify.api.translator.MarkerProcessorFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.engine.FormatterEngineFactoryService;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.obfl.ExpressionFactory;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.writer.PagedMediaWriter;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class LayoutEngineFactoryImpl implements FormatterEngineFactoryService {
	private FormatterFactory ff;
	private MarkerProcessorFactoryMakerService mpf;
	private TextBorderFactoryMakerService tbf;
	private ExpressionFactory ef;
	private XMLInputFactory in;
	private XMLEventFactory xef;
	private XMLOutputFactory of;

	public LayoutEngineImpl newFormatterEngine(FilterLocale locale, String mode, PagedMediaWriter writer) {
		return new LayoutEngineImpl(locale, mode, writer, ff, mpf, tbf, ef, in, xef, of);
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
	
	@Reference
	public void setXMLInputFactory(XMLInputFactory in) {
		this.in = in;
	}

	public void unsetXMLInputFactory(XMLInputFactory in) {
		this.in = null;
	}

	@Reference
	public void setXMLEventFactory(XMLEventFactory xef) {
		this.xef = xef;
	}

	public void unsetXMLEventFactory(XMLEventFactory xef) {
		this.xef = null;
	}

	@Reference
	public void setXMLOutputFactory(XMLOutputFactory of) {
		this.of = of;
	}
	
	public void unsetXMLOutputFactory(XMLOutputFactory of) {
		this.of = null;
	}

}
