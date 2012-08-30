package org.daisy.dotify.setups;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.system.InputManager;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.ResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.ValidatorTask;
import org.daisy.dotify.system.XsltTask;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.xml.sax.SAXException;

/**
 * <p>Provides a method to determine the input format and load the 
 * appropriate settings based on the detected input format.</p>
 * 
 * <p>The InputDetectorTaskSystem is specifically designed to aid 
 * the process of selecting and executing the correct validation rules 
 * and transformation for a given input document and locale.</p>
 * 
 * <p>Note that, input format must be well-formed XML.</p>
 * 
 * <p>Resources are located in the following order:</p>
 * <ul> 
 * <li>localBase/[output format]/[input format].properties</li>
 * <li>localBase/[output format]/xml.properties</li>
 * <li>commonBase/[output format]/[input format].properties</li>
 * <li>commonBase/[output format]/xml.properties</li>
 * </ul>
 * <p>The properties file for the format should contain two entries:</p>
 * <ul>
 * <li>&lt;entry key="validation"&gt;path/to/schema/file&lt;/entry&gt;</li>
 * <li>&lt;entry key="transformation"&gt;path/to/xslt/file&lt;/entry&gt;</li>
 * </ul>
 * <p>Paths in the properties file are relative to the resource base url.</p>
 * <p>Whitespace normalization of the OBFL file is added last in the chain.</p>
 * 
 * @author Joel Håkansson, TPB
 *
 */
class XMLInputManager implements InputManager {
	//private final URL resourceBase;
	private final static String PRESETS_PATH = "presets/";
	private final static String CONFIG_PATH = "config/";
	private final ResourceLocator localLocator;
	private final ResourceLocator commonLocator;
	private final String name;
	private final Logger logger;

	/**
	 * Create a new InputDetectorTaskSystem. 
	 * @param locator the resource locator root 
	 * @param localBase a path relative the resource root to the local resources
	 * @param commonBase a path relative the resource root to the common resources
	 */
	XMLInputManager(ResourceLocator localLocator, ResourceLocator commonLocator) {
		this(localLocator, commonLocator, "InputDetectorTaskSystem");
	}
	
	XMLInputManager(ResourceLocator localLocator, ResourceLocator commonLocator, String name) {
		//this.resourceBase = resourceBase;
		this.localLocator = localLocator;
		this.commonLocator = commonLocator;
		this.name = name;
		this.logger = Logger.getLogger(XMLInputManager.class.getCanonicalName());
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(RunParameters parameters)
			throws TaskSystemException {

		String input = parameters.getProperty(SystemKeys.INPUT);
		String inputformat = null;
		Peeker peeker = null;
		try {
			PeekResult peekResult;
			peeker = PeekerPool.getInstance().acquire();
			FileInputStream is = new FileInputStream(new File(input));
			peekResult = peeker.peek(is);
			String rootNS = peekResult.getRootElementNsUri();
			String rootElement = peekResult.getRootElementLocalName();
			Properties p = new Properties();
			p.loadFromXML(new DefaultConfigUrlResourceLocator().getInputFormatCatalogResourceURL().openStream());
			if (rootNS!=null) {
				inputformat = p.getProperty(rootElement+"@"+rootNS);
				if (inputformat !=null && "".equals(inputformat)) {
					return new ArrayList<InternalTask>();
				}
				/*
				if (rootNS.equals("http://www.daisy.org/z3986/2005/dtbook/") && rootElement.equals("dtbook")) {
					inputformat = "dtbook.properties";
				} else if (rootNS.equals("http://www.daisy.org/ns/2011/obfl") && rootElement.equals("obfl")) {
					//no transformation or validation required, we're ready.
					return new ArrayList<InternalTask>();
				}*/
				// else if {
					// Add more input formats here...
				// }
			} else {
				inputformat = p.getProperty(rootElement);
				if (inputformat !=null && "".equals(inputformat)) {
					return new ArrayList<InternalTask>();
				}
			}
			// TODO: if this becomes a documented feature of the system, then a namespace should be added... 
			/*
			else if (rootElement.equals("obfl")) {
				//no transformation or validation required, we're ready.
				return new ArrayList<InternalTask>();
			}*/
			is.close();
		} catch (SAXException e) {
			throw new TaskSystemException("SAXException while reading input", e);
		} catch (IOException e) {
			throw new TaskSystemException("IOException while reading input", e);
		}  finally {
			if (peeker!=null) {
				PeekerPool.getInstance().release(peeker);
			}
		}
		String xmlformat = "xml.properties";
		String outputformat = parameters.getProperty(SystemKeys.OUTPUT_FORMAT).toLowerCase();

		String basePath = CONFIG_PATH + outputformat + "/";

		if (inputformat!=null) {
			try {
				return readConfiguration(localLocator, localLocator.getResource(basePath + inputformat), parameters);
			} catch (ResourceLocatorException e) {
				logger.fine("Cannot find URL " + basePath + inputformat);
			}
		}
		try {
			return readConfiguration(localLocator, localLocator.getResource(basePath + xmlformat), parameters);
		} catch (ResourceLocatorException e) {
			logger.fine("Cannot find URL " + basePath + xmlformat);
		}
		if (inputformat!=null) {
			try {
				return readConfiguration(commonLocator, commonLocator.getResource(basePath + inputformat), parameters);
			} catch (ResourceLocatorException e) {
				logger.fine("Cannot find URL " + basePath + inputformat);
			}
		}
		try {
			return readConfiguration(commonLocator, commonLocator.getResource(basePath + xmlformat), parameters);
		} catch (ResourceLocatorException e) {
			logger.fine("Cannot find URL " + basePath + xmlformat);
		}
		throw new TaskSystemException("Unable to open a configuration stream for the format.");
	}
	
	private ArrayList<InternalTask> readConfiguration(ResourceLocator locator, URL t, RunParameters parameters) throws TaskSystemException {
		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		try {
			InputStream propsStream = null;
			try {
				propsStream = t.openStream();
				logger.fine("Opening stream: " + t.getFile());
			} catch (IOException e) {
				logger.log(Level.FINE, "Cannot open stream: " + t.getFile(), e);
				throw new ResourceLocatorException("Cannot open stream");
			}
			if (propsStream != null) {
				Properties p = new Properties();
				p.loadFromXML(propsStream);
				propsStream.close();


				HashMap xsltProps = new HashMap();
				xsltProps.putAll(parameters.getProperties());
				for (Object key : p.keySet()) {
					String[] schemas = p.get(key).toString().split("\\s*,\\s*");
					if ("validation".equals(key.toString())) {
						for (String s : schemas) {
							if (s!=null && s!="") {
								setup.add(new ValidatorTask("Conformance checker: " + s, locator.getResource(s)));
							}
						}
					} else if ("transformation".equals(key.toString())) {
						for (String s : schemas) {
							if (s!=null && s!="") {
								setup.add(new XsltTask("Input to FLOW converter: " + s, locator.getResource(s), null, xsltProps));
							}
						}
					} else {
						logger.info("Unrecognized key: " + key);
					}
				}
			} else {
				throw new TaskSystemException("Unable to open a configuration stream for the format.");
			}

		} catch (InvalidPropertiesFormatException e) {
			throw new TaskSystemException("Unable to read settings file.", e);
		} catch (IOException e) {
			throw new TaskSystemException("Unable to open settings file.", e);
		}
		return setup;
	}

	public URL getConfigurationURL(String identifier) throws ResourceLocatorException {
		ConfigUrlLocator c = new ConfigUrlLocator();
		String subPath = c.getSubpath(identifier);
		if (subPath==null) {
        	// try identifier as path
        	try {
        		return new URL(identifier);
        	} catch (MalformedURLException e) {
        		throw new IllegalArgumentException("Cannot find configuration for " + identifier);
        	}
		} else {
			try {
				return localLocator.getResource(PRESETS_PATH + subPath);
			} catch (ResourceLocatorException e) {
				// try common locator
				
			}
			return commonLocator.getResource(PRESETS_PATH + subPath);
		}
	}

}
