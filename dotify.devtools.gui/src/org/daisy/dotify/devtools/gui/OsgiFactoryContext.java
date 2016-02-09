package org.daisy.dotify.devtools.gui;

import org.daisy.braille.api.embosser.EmbosserCatalogService;
import org.daisy.braille.api.paper.PaperCatalogService;
import org.daisy.braille.api.table.TableCatalogService;
import org.daisy.braille.api.validator.ValidatorFactoryService;
import org.daisy.dotify.api.engine.FormatterEngineFactoryService;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriterFactoryService;
import org.osgi.framework.BundleContext;

public class OsgiFactoryContext implements FactoryContext {
	private TranslatorTracker tracker;
	private TableCatalogTracker tctracker;
	private PaperCatalogTracker pctracker;
	private EmbosserCatalogTracker ectracker;
	private ValidatorTracker vtracker;
	private Int2TextTracker itracker;
	private HyphTracker htracker;
	private FormatterTracker ftracker;
	private WriterTracker wtracker;

	public void openTracking(BundleContext context) {
		tracker = new TranslatorTracker(context);
		tracker.open();
		tctracker = new TableCatalogTracker(context);
		tctracker.open();
		pctracker = new PaperCatalogTracker(context);
		pctracker.open();
		ectracker = new EmbosserCatalogTracker(context);
		ectracker.open();
		vtracker = new ValidatorTracker(context);
		vtracker.open();
		itracker = new Int2TextTracker(context);
		itracker.open();
		htracker = new HyphTracker(context);
		htracker.open();
		ftracker = new FormatterTracker(context);
		ftracker.open();
		wtracker = new WriterTracker(context);
		wtracker.open();
	}

	public void closeTracking() {
		tracker.close();
		tctracker.close();
		pctracker.close();
		ectracker.close();
		vtracker.close();
		itracker.close();
		htracker.close();
		ftracker.close();
		wtracker.close();
	}

	@Override
	public EmbosserCatalogService getEmbosserCatalogService() {
		return ectracker.get();
	}

	@Override
	public PaperCatalogService getPaperCatalogService() {
		return pctracker.get();
	}

	@Override
	public TableCatalogService getTableCatalogService() {
		return tctracker.get();
	}

	@Override
	public BrailleTranslatorFactoryMakerService getBrailleTranslatorFactoryMakerService() {
		return tracker.get();
	}

	@Override
	public ValidatorFactoryService getValidatorFactoryService() {
		return vtracker.get();
	}

	@Override
	public Integer2TextFactoryMakerService getInteger2TextFactoryMakerService() {
		return itracker.get();
	}

	@Override
	public HyphenatorFactoryMakerService getHyphenatorFactoryMakerService() {
		return htracker.get();
	}

	@Override
	public FormatterEngineFactoryService getFormatterEngineFactoryService() {
		return ftracker.get();
	}

	@Override
	public PagedMediaWriterFactoryService getPagedMediaWriterFactoryService() {
		return wtracker.get();
	}

}
