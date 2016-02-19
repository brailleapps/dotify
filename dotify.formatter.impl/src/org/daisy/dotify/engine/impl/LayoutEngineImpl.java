package org.daisy.dotify.engine.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.api.engine.FormatterEngine;
import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.MetaDataItem;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.api.writer.PagedMediaWriterException;
import org.daisy.dotify.obfl.OBFLParserException;
import org.daisy.dotify.obfl.OBFLWsNormalizer;
import org.daisy.dotify.obfl.ObflParser;

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
class LayoutEngineImpl implements FormatterEngine {
	private final static String DC_NS = "http://purl.org/dc/elements/1.1/";
	private final static QName DC_IDENTIFIER = new QName(DC_NS, "identifier");
	private final static QName DC_DATE = new QName(DC_NS, "date");
	private final static QName DC_FORMAT = new QName(DC_NS, "format");
	private final String locale;
	private final String mode;
	private final PagedMediaWriter writer;
	private final Logger logger;
	private boolean normalize;
	private final FactoryManager fm;
	
	/**
	 * Creates a new instance of LayoutEngineTask.
	 * @param name a descriptive name for the task
	 * @param translator the translator to use
	 * @param writer the output writer
	 */
	public LayoutEngineImpl(String locale, String mode, PagedMediaWriter writer, FactoryManager fm) {
		this.locale = locale;
		this.mode = mode;
		//this.locale = locale;
		this.writer = writer;
		this.logger = Logger.getLogger(LayoutEngineImpl.class.getCanonicalName());
		this.normalize = true;
		this.fm = fm;
	}

	public boolean isNormalizing() {
		return normalize;
	}

	public void setNormalizing(boolean normalize) {
		this.normalize = normalize;
	}

	@Override
	public void convert(InputStream input, OutputStream output) throws LayoutEngineException {
		File f = null;
		try {
			if (normalize) {
				logger.info("Normalizing obfl...");
				try {
					f = File.createTempFile("temp", ".tmp");
					f.deleteOnExit();
					OBFLWsNormalizer normalizer = new OBFLWsNormalizer(fm.getXmlInputFactory().createXMLEventReader(input), fm.getXmlEventFactory(), new FileOutputStream(f));
					normalizer.parse(fm.getXmlOutputFactory());
					try {
						input.close();
					} catch (Exception e) {
						logger.log(Level.FINE, "Failed to close stream.", e);
					}
					input = new FileInputStream(f);
				} catch (Exception e) {
					throw new LayoutEngineException(e);
				}
			}
			try {
				logger.info("Parsing input...");

				ObflParser obflParser = new ObflParser(locale, mode, fm);
				obflParser.parse(fm.getXmlInputFactory().createXMLEventReader(input));

				try {
					input.close();
				} catch (Exception e) {
					logger.log(Level.FINE, "Failed to close stream.", e);
				}

				logger.info("Rendering output...");
				ArrayList<MetaDataItem> meta = new ArrayList<>();
				for (MetaDataItem item : obflParser.getMetaData()) {
					// Filter out identifier, date and format from the OBFL meta data
					// because the meta data in the OBFL file is about itself, and these properties are not transferable
					if (!(item.getKey().equals(DC_IDENTIFIER) || item.getKey().equals(DC_DATE) || item.getKey().equals(DC_FORMAT))) {
						meta.add(item);
					}
				}
				writer.prepare(meta);
				writer.open(output);
				obflParser.writeResult(writer);

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
		} finally {
			if (f != null) {
				if (!f.delete()) {
					f.deleteOnExit();
				}
			}
		}
	}

}
 