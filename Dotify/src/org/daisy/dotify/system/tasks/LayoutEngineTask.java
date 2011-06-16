package org.daisy.dotify.system.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.validation.Schema;

import org.daisy.dotify.formatter.BookStruct;
import org.daisy.dotify.formatter.DefaultBookStruct;
import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.PageStruct;
import org.daisy.dotify.formatter.PagedMediaWriter;
import org.daisy.dotify.formatter.PagedMediaWriterException;
import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.PaginatorFactory;
import org.daisy.dotify.formatter.PaginatorHandler;
import org.daisy.dotify.formatter.StaxFlowHandler;
import org.daisy.dotify.formatter.VolumeSplitterFactory;
import org.daisy.dotify.formatter.WriterHandler;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.InternalTaskException;
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
	        XMLInputFactory inFactory = XMLInputFactory.newInstance();
			inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
	        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
	        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
	        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
	        XMLEventReader reader = inFactory.createXMLEventReader(new FileInputStream(input));
	        
			FormatterFactory formatterFactory = FormatterFactory.newInstance();
			formatterFactory.setFilterFactory(filterFactory);
			formatterFactory.setLocale(locale);
			
			Formatter performer = formatterFactory.newFormatter();
			
			logger.info("Reading input...");
/* SAX impl
			performer.open();
			FlowHandler flow = new FlowHandler(performer);
			sp.parse(input, flow);*/
			
	        StaxFlowHandler flow = new StaxFlowHandler(performer);
	        flow.parse(reader);

			performer.close();
			
			logger.info("Paginating...");
			Paginator paginator = PaginatorFactory.newInstance().newPaginator();
			paginator.open();

			PaginatorHandler.paginate(performer.getFlowStruct(), paginator);
			paginator.close();

			PageStruct pageStruct = paginator.getPageStruct();

			//FIXME: remove factory here:
			BookStruct bookStruct = new DefaultBookStruct(pageStruct, flow.getMasters(), flow.getVolumeTemplates(), flow.getTocs(), formatterFactory);
			VolumeSplitterFactory splitterFactory = VolumeSplitterFactory.newInstance();
			
			//FIXME: add target size variable (use splitterMax?)
			//splitterFactory.setTargetVolumeSize(targetVolumeSize);

			logger.info("Writing file...");
			writer.open(new FileOutputStream(output));
			WriterHandler.write(bookStruct, filterFactory, locale, splitterFactory, writer);
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
		}
	}

}
 