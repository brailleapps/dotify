package org.daisy.dotify.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.book.BookStruct;
import org.daisy.dotify.formatter.FormatterException;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.obfl.ObflParser;
import org.daisy.dotify.obfl.ObflResourceLocator;
import org.daisy.dotify.obfl.ObflResourceLocator.ObflResourceIdentifier;
import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorFactory;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.writer.PagedMediaWriter;
import org.daisy.dotify.writer.PagedMediaWriterException;
import org.daisy.dotify.writer.Volume;
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
public class LayoutEngineTask extends ReadWriteTask  {
	private final BrailleTranslator translator;
	//private final FilterLocale locale;
	private final PagedMediaWriter writer;
	private final Logger logger;
	
	/**
	 * Creates a new instance of LayoutEngineTask.
	 * @param name a descriptive name for the task
	 * @param translator the translator to use
	 * @param writer the output writer
	 */
	public LayoutEngineTask(String name, BrailleTranslator translator, PagedMediaWriter writer) {
		super(name);
		this.translator = translator;
		//this.locale = locale;
		this.writer = writer;
		this.logger = Logger.getLogger(LayoutEngineTask.class.getCanonicalName());
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
		
			logger.info("Validating input...");
			
			try {
				ValidatorTask.validate(input, ObflResourceLocator.getInstance().getResourceByIdentifier(ObflResourceIdentifier.OBFL_XML_SCHEMA));
			} catch (ValidatorException e) {
				throw new InternalTaskException("Input validation failed.", e);
			}

			logger.info("Parsing input...");

			FormatterFactory formatterFactory = FormatterFactory.newInstance();
			formatterFactory.setTranslator(translator);
			//formatterFactory.setLocale(locale);

	        ObflParser obflParser = new ObflParser(formatterFactory);
	        //obflParser.setPaginatorFactory(PaginatorFactory.newInstance());
			//VolumeSplitterFactory splitterFactory = VolumeSplitterFactory.newInstance();
			//obflParser.setVolumeSplitterFactory(splitterFactory);
			obflParser.parse(new FileInputStream(input));

			logger.info("Working...");
			PaginatorFactory paginatorFactory = PaginatorFactory.newInstance();
			Paginator paginator = paginatorFactory.newPaginator();
			paginator.open(formatterFactory, obflParser.getBlockStruct().getBlockSequenceIterable());
			
			BookStruct bookStruct = new BookStruct(
					paginator,
					obflParser.getVolumeContentFormatter(),
					formatterFactory,
					paginatorFactory
				);
			Iterable<Volume> volumes = bookStruct.getVolumes();

			logger.info("Rendering output...");
			writer.open(new FileOutputStream(output));
			//splitterFactory.newSplitter().split(bookStruct)
			String ms = translator.translate(" ").getTranslatedRemainder();

			WriterHandler wh = new WriterHandler(ms);
			wh.write(volumes, writer);
			writer.close();

		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException while running task. ", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException while running task. ", e);
		} catch (PagedMediaWriterException e) {
			throw new InternalTaskException("Could not open media writer.", e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException("XMLStreamException while running task.", e);
		} catch (FormatterException e) {
			throw new InternalTaskException("FormatterException while running task.", e);
		}
	}

}
 