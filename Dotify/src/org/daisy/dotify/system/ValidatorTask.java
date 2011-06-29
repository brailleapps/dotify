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
public class ValidatorTask extends InternalTask implements ErrorHandler {
	final static String SCHEMATRON_PROPERTY_KEY = "javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron";
	final static String SCHEMATRON_PROPERTY_VALUE = "org.daisy.util.xml.validation.jaxp.SchematronSchemaFactory";
	private URL schema;
	private boolean error = false;
	private final Logger logger;

	public ValidatorTask(String name, URL schema) {
		super(name);
		this.schema = schema;
		this.logger = Logger.getLogger(ValidatorTask.class.getCanonicalName());
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			if (System.getProperty(SCHEMATRON_PROPERTY_KEY)==null) {
				logger.info("System property \"" + SCHEMATRON_PROPERTY_KEY + "\" not set");
				logger.info("Setting property \"" + SCHEMATRON_PROPERTY_KEY + "\" to " + SCHEMATRON_PROPERTY_VALUE);
				System.setProperty(SCHEMATRON_PROPERTY_KEY, SCHEMATRON_PROPERTY_VALUE);
			}
			SimpleValidator sv = new SimpleValidator(schema, this);
			boolean ret = sv.validate(input.toURI().toURL());
			FileUtils.copy(input, output);
			if (ret && !error) {
				return;
			}
		} catch (SAXException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (TransformerException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (ValidationException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (MalformedURLException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (IOException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		}
		throw new InternalTaskException("Input validation failed.");
	}

	public void error(SAXParseException exception) throws SAXException {
		/*EventBus.getInstance().publish(new MessageEvent(this, exception.getMessage().replaceAll("\\s+", " "), Type.ERROR));*/
		logger.log(Level.WARNING, "SAXParseException in validator task", exception);
		error = true;
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		throw new SAXException(exception);
	}

	public void warning(SAXParseException exception) throws SAXException {
		System.err.println(exception.toString());
	}

}
