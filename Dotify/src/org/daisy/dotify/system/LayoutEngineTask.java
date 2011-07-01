package org.daisy.dotify.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.validation.Schema;

import org.daisy.dotify.formatter.FormatterException;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.PagedMediaWriter;
import org.daisy.dotify.formatter.PagedMediaWriterException;
import org.daisy.dotify.formatter.PaginatorFactory;
import org.daisy.dotify.formatter.VolumeSplitterFactory;
import org.daisy.dotify.formatter.WriterHandler;
import org.daisy.dotify.formatter.core.ObflParser;
import org.daisy.dotify.formatter.dom.VolumeStruct;
import org.daisy.dotify.text.FilterFactory;
import org.daisy.dotify.text.FilterLocale;

//TODO: Validate against schema

/**
 * <p>
 * The LayoutEngineTask converts a FLOW-file into a file format defined by the
 * supplied {@link PagedMediaWriter}.</p>
 * 
 * <p>The LayoutEngineTask is an advanced text-only layout system.</p>
 * <p>Input file must be of type FLOW.</p>
 * <p>The rendering is done in two steps:</p>
 * <ol>
 * 	<li></li>
 * </ol>
 * @author Joel Håkansson, TPB
 *
 */
public class LayoutEngineTask extends InternalTask  {
	private final FilterFactory filterFactory;
	private final FilterLocale locale;
	private final PagedMediaWriter writer;
	private final Logger logger;
	private Schema schema;
	
	/**
	 * Create a new instance of LayoutEngineTask.
	 * @param name a descriptive name for the task
	 * @param flow 
	 * @param paginator
	 * @param writer
	 */
	public LayoutEngineTask(String name, FilterFactory filterFactory, FilterLocale locale, PagedMediaWriter writer) {
		super(name);
		this.filterFactory = filterFactory;
		this.locale = locale;
		this.writer = writer;
		this.schema = null;
		this.logger = Logger.getLogger(LayoutEngineTask.class.getCanonicalName());
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
/* SAX impl
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			if (schema != null) {
				spf.setSchema(schema);
			}
			SAXParser sp = spf.newSAXParser();*/

			logger.info("Parsing input...");

			
/* SAX impl
			performer.open();
			FlowHandler flow = new FlowHandler(performer);
			sp.parse(input, flow);*/

			FormatterFactory formatterFactory = FormatterFactory.newInstance();
			formatterFactory.setFilterFactory(filterFactory);
			formatterFactory.setLocale(locale);

	        ObflParser obflParser = new ObflParser(formatterFactory);
	        obflParser.setPaginatorFactory(PaginatorFactory.newInstance());
			//FIXME: add target size variable (use splitterMax?)
			//splitterFactory.setTargetVolumeSize(targetVolumeSize);
			VolumeSplitterFactory splitterFactory = VolumeSplitterFactory.newInstance();
			obflParser.setVolumeSplitterFactory(splitterFactory);
	        VolumeStruct volumes = obflParser.parse(new FileInputStream(input));
/*
			logger.info("Paginating...");
			Paginator paginator = PaginatorFactory.newInstance().newPaginator();
			paginator.open(formatterFactory);

			PaginatorHandler.paginate(flow.getBlockStruct().getBlockSequenceIterable(), paginator);
			paginator.close();

			PageStruct pageStruct = paginator.getPageStruct();
			


			BookStruct bookStruct = new DefaultBookStruct(
					pageStruct,
					flow.getMasters(),
					flow.getVolumeTemplates(),
					flow.getTocs(),
					formatterFactory
				);
*/
			logger.info("Rendering output...");
			writer.open(new FileOutputStream(output));
			//splitterFactory.newSplitter().split(bookStruct)
			WriterHandler.write(volumes, writer);
			writer.close();

		/*} catch (SAXException e) {
			throw new InternalTaskException("SAXException while runing task.", e);*/
		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException while running task. ", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException while running task. ", e);
		/*} catch (ParserConfigurationException e) {
			throw new InternalTaskException("ParserConfigurationException while running task. ", e);*/
		} catch (PagedMediaWriterException e) {
			throw new InternalTaskException("Could not open media writer.", e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException("XMLStreamException while running task.", e);
		} catch (FormatterException e) {
			throw new InternalTaskException("FormatterException while running task.", e);
		}
	}

}
 