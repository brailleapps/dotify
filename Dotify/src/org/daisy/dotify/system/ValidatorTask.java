package org.daisy.dotify.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.common.xml.EntityResolverCache;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;

/**
 * <p>This task validates the input file against the given schema. The 
 * task throws an exception if anything goes wrong.</p>
 * 
 * <p>Input file type requirement: XML</p>
 * 
 * @author Joel Håkansson
 */
public class ValidatorTask extends ReadOnlyTask {
	private URL schema;

	public ValidatorTask(String name, URL schema) {
		super(name);
		this.schema = schema;
	}
	
	public static boolean validate(File input, URL schema) throws ValidatorException {
		ValidatorTaskErrorHandler errorHandler = new ValidatorTaskErrorHandler();
		try {
			boolean ret = runValidation(input.toURI().toURL(), schema, errorHandler);
			return ret && !errorHandler.hasError();
		} catch (MalformedURLException e) {
			throw new ValidatorException("Validation failed.", e);
		}
	}
	
	private static boolean runValidation(URL url, URL schema, ErrorHandler errorHandler) {
		
		PropertyMapBuilder propertyBuilder = new PropertyMapBuilder();

        try {
    		propertyBuilder.put(ValidateProperty.ERROR_HANDLER, errorHandler);
    		propertyBuilder.put(ValidateProperty.ENTITY_RESOLVER, new EntityResolverCache());
    		PropertyMap map = propertyBuilder.toPropertyMap();
            ValidationDriver vd = new ValidationDriver(map);
			vd.loadSchema(configureInputSource(schema));
			return vd.validate(configureInputSource(url));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static InputSource configureInputSource(URL url) throws IOException, URISyntaxException {
		InputSource is = new InputSource(url.openStream());
		is.setSystemId(url.toURI().toString());
		return is;
	}

	@Override
	public void execute(File input) throws InternalTaskException {
		try {
			boolean ret = validate(input, schema);
			//FileUtils.copy(input, output);
			if (ret) {
				return;
			}
		} /*catch (IOException e) {
			throw new InternalTaskException("Failed to copy file.", e);
		} */catch (ValidatorException e) {
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
			logger.log(Level.WARNING, "Validation error " + getLineColumn(exception), exception.getMessage());
			error = true;
		}
	
		public void fatalError(SAXParseException exception) throws SAXException {
			throw new SAXException(exception);
		}
	
		public void warning(SAXParseException exception) throws SAXException {
			logger.log(Level.INFO, "Parse warning " + getLineColumn(exception), exception);
		}
		
		private String getLineColumn(SAXParseException e) {
			if (e.getLineNumber()<0 && e.getColumnNumber()<0) {
				return "";
			} else { 
				boolean both = (e.getLineNumber()>=0 && e.getColumnNumber()>=0);
				return "at "+(e.getLineNumber()>=0?"line: "+e.getLineNumber():"")+
						(both?" ":"")+
						(e.getColumnNumber()>=0?"column: "+e.getColumnNumber():"")
						+")";
			}
		}
	}

}
