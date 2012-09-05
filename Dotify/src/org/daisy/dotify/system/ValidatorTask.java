package org.daisy.dotify.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;

import org.daisy.util.file.FileUtils;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p>This tasks validates the input file against the given schema and copies the original file to 
 * the output file, to conform with the contract of an {@link InternalTask}.</p>
 * <p>The tasks throws an exception if anything goes wrong.</p>
 * <p>Input file type requirement: XML</p>
 * @author Joel Håkansson, TPB
 *
 */
public class ValidatorTask extends InternalTask {
	final static String SCHEMATRON_PROPERTY_KEY = "javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron";
	final static String SCHEMATRON_PROPERTY_VALUE = "org.daisy.util.xml.validation.jaxp.SchematronSchemaFactory";
	private URL schema;

	public ValidatorTask(String name, URL schema) {
		super(name);
		this.schema = schema;
	}
	
	public static boolean validate(File input, URL schema) throws ValidatorException {
		if (System.getProperty(SCHEMATRON_PROPERTY_KEY)==null) {
			Logger logger = Logger.getLogger(ValidatorTask.class.getCanonicalName());
			logger.info("System property \"" + SCHEMATRON_PROPERTY_KEY + "\" not set");
			logger.info("Setting property \"" + SCHEMATRON_PROPERTY_KEY + "\" to " + SCHEMATRON_PROPERTY_VALUE);
			System.setProperty(SCHEMATRON_PROPERTY_KEY, SCHEMATRON_PROPERTY_VALUE);
		}
		ValidatorTaskErrorHandler errorHandler = new ValidatorTaskErrorHandler();
		try {
			SimpleValidator sv = new SimpleValidator(schema, errorHandler);
			boolean ret = sv.validate(input.toURI().toURL());
			return ret && !errorHandler.hasError();
		} catch (MalformedURLException e) {
			throw new ValidatorException("Validation failed.", e);
		} catch (SAXException e) {
			throw new ValidatorException("Validation failed.", e);
		} catch (TransformerException e) {
			throw new ValidatorException("Validation failed.", e);
		} catch (ValidationException e) {
			throw new ValidatorException("Validation failed.", e);
		}
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			boolean ret = validate(input, schema);
			FileUtils.copy(input, output);
			if (ret) {
				return;
			}
		} catch (IOException e) {
			throw new InternalTaskException("Failed to copy file.", e);
		} catch (ValidatorException e) {
			throw new InternalTaskException("Validation failed.", e);
		}
		throw new InternalTaskException("Validation failed.");
	}
	
	private static class ValidatorTaskErrorHandler implements ErrorHandler {
		private final Logger logger;
		private boolean error = false;
		
		public ValidatorTaskErrorHandler() {
			this.logger = Logger.getLogger(ValidatorTaskErrorHandler.class.getCanonicalName());
		}
		
		public boolean hasError() {
			return error;
		}
	
		public void error(SAXParseException exception) throws SAXException {
			logger.log(Level.WARNING, "SAXParseException in validator task: " + exception.getMessage());
			error = true;
		}
	
		public void fatalError(SAXParseException exception) throws SAXException {
			throw new SAXException(exception);
		}
	
		public void warning(SAXParseException exception) throws SAXException {
			logger.log(Level.INFO, "Parse warning.", exception);
		}
	}

}
