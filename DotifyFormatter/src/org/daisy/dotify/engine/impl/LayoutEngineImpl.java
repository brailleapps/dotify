package org.daisy.dotify.engine.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.api.translator.MarkerProcessor;
import org.daisy.dotify.api.translator.MarkerProcessorConfigurationException;
import org.daisy.dotify.api.translator.MarkerProcessorFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.engine.FormatterEngine;
import org.daisy.dotify.engine.LayoutEngineException;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.obfl.ExpressionFactory;
import org.daisy.dotify.obfl.OBFLParserException;
import org.daisy.dotify.obfl.OBFLWsNormalizer;
import org.daisy.dotify.obfl.ObflParser;
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
class LayoutEngineImpl implements FormatterEngine {
	private final FilterLocale locale;
	private final String mode;
	private final PagedMediaWriter writer;
	private final Logger logger;
	private boolean normalize;
	private final FormatterFactory ff;
	private final MarkerProcessorFactoryMakerService mpf;
	private final TextBorderFactoryMakerService tbf;
	private final ExpressionFactory ef;
	private final XMLInputFactory in;
	private final XMLEventFactory xef;
	private final XMLOutputFactory of;
	
	/**
	 * Creates a new instance of LayoutEngineTask.
	 * @param name a descriptive name for the task
	 * @param translator the translator to use
	 * @param writer the output writer
	 */
	public LayoutEngineImpl(FilterLocale locale, String mode, PagedMediaWriter writer, FormatterFactory ff, MarkerProcessorFactoryMakerService mpf, TextBorderFactoryMakerService tbf, ExpressionFactory ef, XMLInputFactory in, XMLEventFactory xef, XMLOutputFactory of) {
		this.locale = locale;
		this.mode = mode;
		//this.locale = locale;
		this.writer = writer;
		this.logger = Logger.getLogger(LayoutEngineImpl.class.getCanonicalName());
		this.normalize = true;
		this.ff = ff;
		this.mpf = mpf;
		this.tbf = tbf;
		this.ef = ef;
		this.of = of;
		this.xef = xef;
		this.in = in;
	}

	public boolean isNormalizing() {
		return normalize;
	}

	public void setNormalizing(boolean normalize) {
		this.normalize = normalize;
	}

	public void convert(InputStream input, OutputStream output) throws LayoutEngineException {
		File f = null;
		try {
			if (normalize) {
				logger.info("Normalizing obfl...");
				try {
					f = File.createTempFile("temp", ".tmp");
					f.deleteOnExit();
					OBFLWsNormalizer normalizer = new OBFLWsNormalizer(in.createXMLEventReader(input), xef, new FileOutputStream(f));
					normalizer.parse(of);
					input = new FileInputStream(f);
				} catch (Exception e) {
					throw new LayoutEngineException(e);
				}
			}
			try {
				logger.info("Parsing input...");

				MarkerProcessor mp;
				try {
					mp = mpf.newMarkerProcessor(locale.toString(), mode);
				} catch (MarkerProcessorConfigurationException e) {
					throw new IllegalArgumentException(e);
				}
				ObflParser obflParser = new ObflParser(locale, mode, mp, ff, tbf, ef);
				obflParser.parse(in.createXMLEventReader(input));

				logger.info("Rendering output...");
				writer.open(output, obflParser.getMetaData());

				WriterHandler wh = new WriterHandler();
				wh.write(obflParser.getFormattedResult(), writer);
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
		} finally {
			if (f != null) {
				if (!f.delete()) {
					f.deleteOnExit();
				}
			}
		}
	}

}
 