package org.daisy.dotify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.book.BookStruct;
import org.daisy.dotify.book.Volume;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.obfl.OBFLParserException;
import org.daisy.dotify.obfl.ObflParser;
import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorFactoryMaker;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.writer.PagedMediaWriter;
import org.daisy.dotify.writer.PagedMediaWriterException;
import org.daisy.dotify.writer.WriterHandler;

/**
 * <p>
 * The LayoutEngineTask converts an OBFL-file into a file format defined by the
 * supplied {@link PagedMediaWriter}.</p>
 * 
 * <p>The LayoutEngineTask is an advanced text-only layout system.</p>
 * <p>Input file must be of type OBFL.</p>
 * 
 * @author Joel Håkansson
 *
 */
public class LayoutEngine  {
	private final BrailleTranslator translator;
	private final FilterLocale locale;
	private final String mode;
	private final PagedMediaWriter writer;
	private final Logger logger;
	
	/**
	 * Creates a new instance of LayoutEngineTask.
	 * @param name a descriptive name for the task
	 * @param translator the translator to use
	 * @param writer the output writer
	 */
	public LayoutEngine(FilterLocale locale, String mode, PagedMediaWriter writer) {
		this.locale = locale;
		this.mode = mode;
		this.translator = getTranslator(locale, mode);
		//this.locale = locale;
		this.writer = writer;
		this.logger = Logger.getLogger(LayoutEngine.class.getCanonicalName());
	}

	private BrailleTranslator getTranslator(FilterLocale locale, String mode) {
		try {
			return BrailleTranslatorFactoryMaker.newInstance().newTranslator(locale.toString(), mode);
		} catch (TranslatorConfigurationException e) {
			return null;
		}
	}

	public void convert(File input, File output) throws LayoutEngineException {
		try {
			logger.info("Parsing input...");
			//formatterFactory.setLocale(locale);
			ObflParser obflParser = new ObflParser(locale, mode);
	        //obflParser.setPaginatorFactory(PaginatorFactory.newInstance());
			//VolumeSplitterFactory splitterFactory = VolumeSplitterFactory.newInstance();
			//obflParser.setVolumeSplitterFactory(splitterFactory);
			obflParser.parse(new FileInputStream(input));

			logger.info("Working...");
			PaginatorFactoryMaker paginatorFactory = PaginatorFactoryMaker.newInstance();
			Paginator paginator = paginatorFactory.newPaginator();
			paginator.open(translator, obflParser.getBlockStruct().getBlockSequenceIterable());
			
			BookStruct bookStruct = new BookStruct(
					paginator,
					obflParser.getVolumeContentFormatter(),
					translator,
					paginatorFactory
				);
			Iterable<Volume> volumes = bookStruct.getVolumes();

			logger.info("Rendering output...");
			writer.open(new FileOutputStream(output), obflParser.getMetaData());
			//splitterFactory.newSplitter().split(bookStruct)

			WriterHandler wh = new WriterHandler();
			wh.write(volumes, writer);
			writer.close();

		} catch (FileNotFoundException e) {
			throw new LayoutEngineException("FileNotFoundException while running task. ", e);
		} catch (IOException e) {
			throw new LayoutEngineException("IOException while running task. ", e);
		} catch (PagedMediaWriterException e) {
			throw new LayoutEngineException("Could not open media writer.", e);
		} catch (XMLStreamException e) {
			throw new LayoutEngineException("XMLStreamException while running task.", e);
		} catch (OBFLParserException e) {
			throw new LayoutEngineException("FormatterException while running task.", e);
		}
	}

}
 