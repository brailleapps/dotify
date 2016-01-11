package org.daisy.dotify.engine.impl;

import org.daisy.dotify.api.engine.FormatterEngineConfigurationException;
import org.daisy.dotify.api.engine.FormatterEngineFactoryService;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.MarkerProcessorFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.formatter.impl.SPIHelper;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class LayoutEngineFactoryImpl implements FormatterEngineFactoryService {
	private FactoryManager factoryManager;

	public LayoutEngineFactoryImpl() {
		factoryManager = new FactoryManager();
	}

	@Override
	public LayoutEngineImpl newFormatterEngine(String locale, String mode, PagedMediaWriter writer) {
		return new LayoutEngineImpl(locale, mode, writer, factoryManager);
	}

	@Override
	@Deprecated
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
		factoryManager.setFormatterFactory(formatterFactory);
	}

	public void unsetFormatterFactory(FormatterFactory formatterFactory) {
		factoryManager.setFormatterFactory(null);
	}

	@Reference
	public void setMarkerProcessor(MarkerProcessorFactoryMakerService mp) {
		factoryManager.setMarkerProcessorFactory(mp);
	}

	public void unsetMarkerProcessor(MarkerProcessorFactoryMakerService mp) {
		factoryManager.setMarkerProcessorFactory(null);
	}

	@Reference
	public void setTextBorderFactoryMaker(TextBorderFactoryMakerService tbf) {
		factoryManager.setTextBorderFactory(tbf);
	}

	public void unsetTextBorderFactoryMaker(TextBorderFactoryMakerService tbf) {
		factoryManager.setTextBorderFactory(null);
	}

	@Reference
	public void setExpressionFactory(ExpressionFactory ef) {
		factoryManager.setExpressionFactory(ef);
	}

	public void unsetExpressionFactory(ExpressionFactory ef) {
		factoryManager.setExpressionFactory(null);
	}

	@Override
	public void setCreatedWithSPI() {
		setFormatterFactory(SPIHelper.getFormatterFactory());
		setMarkerProcessor(SPIHelper.getMarkerProcessorFactoryMaker());
		setTextBorderFactoryMaker(SPIHelper.getTextBorderFactoryMaker());
		setExpressionFactory(SPIHelper.getExpressionFactory());		
	}

}
