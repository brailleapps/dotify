package org.daisy.dotify.devtools.gui;

import java.lang.reflect.Method;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.braille.api.embosser.EmbosserCatalogService;
import org.daisy.braille.api.paper.PaperCatalogService;
import org.daisy.braille.api.table.TableCatalogService;
import org.daisy.braille.api.validator.ValidatorFactoryService;
import org.daisy.dotify.api.engine.FormatterEngineFactoryService;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMakerService;

public class SpiFactoryContext implements FactoryContext {
	private final static Logger logger = Logger.getLogger(SpiFactoryContext.class.getCanonicalName());
	private EmbosserCatalogService ecservice;
	private PaperCatalogService pcservice;
	private TableCatalogService tcservice;
	private BrailleTranslatorFactoryMakerService btfservice;
	private ValidatorFactoryService vfservice;
	private Integer2TextFactoryMakerService i2tfservice;
	private HyphenatorFactoryMakerService hfservice;
	private FormatterEngineFactoryService fefservice;
	private PagedMediaWriterFactoryMakerService pmwfservice;
	
	@Override
	public EmbosserCatalogService getEmbosserCatalogService() {
		if (ecservice==null) {
			ecservice = invokeStatic("org.daisy.braille.consumer.embosser.EmbosserCatalog", "newInstance");
		}
		return ecservice;
	}

	@Override
	public PaperCatalogService getPaperCatalogService() {
		if (pcservice==null) {
			pcservice = invokeStatic("org.daisy.braille.consumer.paper.PaperCatalog", "newInstance");
		}
		return pcservice;
	}

	@Override
	public TableCatalogService getTableCatalogService() {
		if (tcservice==null) {
			tcservice = invokeStatic("org.daisy.braille.consumer.table.TableCatalog", "newInstance");
		}
		return tcservice;
	}

	@Override
	public BrailleTranslatorFactoryMakerService getBrailleTranslatorFactoryMakerService() {
		if (btfservice==null) {
			btfservice = invokeStatic("org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker", "newInstance");
		}
		return btfservice;
	}

	@Override
	public ValidatorFactoryService getValidatorFactoryService() {
		if (vfservice==null) {
			vfservice = invokeStatic("org.daisy.braille.consumer.validator.ValidatorFactory", "newInstance");
		}
		return vfservice;
	}

	@Override
	public Integer2TextFactoryMakerService getInteger2TextFactoryMakerService() {
		if (i2tfservice==null) {
			i2tfservice = invokeStatic("org.daisy.dotify.consumer.text.Integer2TextFactoryMaker", "newInstance");
		}
		return i2tfservice;
	}

	@Override
	public HyphenatorFactoryMakerService getHyphenatorFactoryMakerService() {
		if (hfservice==null) {
			hfservice = invokeStatic("org.daisy.dotify.consumer.hyphenator.HyphenatorFactoryMaker", "newInstance");
		}
		return hfservice;
	}

	@Override
	public FormatterEngineFactoryService getFormatterEngineFactoryService() {
		if (fefservice==null) {
			fefservice = ServiceLoader.load(FormatterEngineFactoryService.class).iterator().next();
			
		}
		return null;
	}

	@Override
	public PagedMediaWriterFactoryMakerService getPagedMediaWriterFactoryService() {
		if (pmwfservice==null) {
			pmwfservice = invokeStatic("org.daisy.dotify.consumer.writer.PagedMediaWriterFactoryMaker", "newInstance");
		}
		return pmwfservice;
	}

	@SuppressWarnings("unchecked")
	private static <T> T invokeStatic(String clazz, String method) {
		T instance = null;
		try {
			Class<?> cls = Class.forName(clazz);
			Method m = cls.getMethod(method);
			instance = (T)m.invoke(null);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to create instance.", e);
		}
		return instance;
	}
}
