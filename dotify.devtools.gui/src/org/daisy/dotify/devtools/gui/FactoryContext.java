package org.daisy.dotify.devtools.gui;

import org.daisy.braille.api.embosser.EmbosserCatalogService;
import org.daisy.braille.api.paper.PaperCatalogService;
import org.daisy.braille.api.table.TableCatalogService;
import org.daisy.braille.api.validator.ValidatorFactoryService;
import org.daisy.dotify.api.engine.FormatterEngineFactoryService;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMakerService;

public interface FactoryContext {

	public EmbosserCatalogService getEmbosserCatalogService();
	public PaperCatalogService getPaperCatalogService();
	public TableCatalogService getTableCatalogService();
	public BrailleTranslatorFactoryMakerService getBrailleTranslatorFactoryMakerService();
	public ValidatorFactoryService getValidatorFactoryService();
	public Integer2TextFactoryMakerService getInteger2TextFactoryMakerService();
	public HyphenatorFactoryMakerService getHyphenatorFactoryMakerService();
	public FormatterEngineFactoryService getFormatterEngineFactoryService();
	public PagedMediaWriterFactoryMakerService getPagedMediaWriterFactoryService();

}
