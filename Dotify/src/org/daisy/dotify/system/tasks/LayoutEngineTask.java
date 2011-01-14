package org.daisy.dotify.system.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.tasks.layout.flow.Flow;
import org.daisy.dotify.system.tasks.layout.impl.FlowHandler;
import org.daisy.dotify.system.tasks.layout.page.PagedMediaWriter;
import org.daisy.dotify.system.tasks.layout.page.PagedMediaWriterException;
import org.daisy.dotify.system.tasks.layout.page.Paginator;
import org.xml.sax.SAXException;

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
	private final Flow performer;
	private Paginator paginator;
	private PagedMediaWriter writer;
	private Schema schema;
	
	/**
	 * Create a new instance of LayoutEngineTask.
	 * @param name a descriptive name for the task
	 * @param flow 
	 * @param paginator
	 * @param writer
	 */
	public LayoutEngineTask(String name, Flow flow, Paginator paginator, PagedMediaWriter writer) {
		super(name);
		this.performer = flow;
		this.paginator = paginator;
		this.writer = writer;
		this.schema = null;
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			FileOutputStream os = new FileOutputStream(output);
			writer.open(os);
			paginator.open(writer);
			performer.open(paginator);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			if (schema != null) {
				spf.setSchema(schema);
			}
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, new FlowHandler(performer));
			performer.close();
			paginator.close();
			writer.close();
		} catch (SAXException e) {
			throw new InternalTaskException("SAXException while runing task.", e);
		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException while runing task. ", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException while runing task. ", e);
		} catch (ParserConfigurationException e) {
			throw new InternalTaskException("ParserConfigurationException while runing task. ", e);
		} catch (PagedMediaWriterException e) {
			throw new InternalTaskException("Could not open media writer.", e);
		}
	}

}
 