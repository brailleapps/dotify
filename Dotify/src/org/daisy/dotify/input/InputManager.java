package org.daisy.dotify.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.ResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystem;
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
 * <p>It can be used as a first step in a TaskSystem, if input format detection
 * is desired.</p>
 * 
 * <p>Note that, input format must be well-formed XML.</p>
 * 
 * <p>Resources are located in the following order:</p>
 * <ul> 
 * <li>resourceBase/localBase/[output format]/[input format].properties</li>
 * <li>resourceBase/localBase/[output format]/xml.properties</li>
 * <li>resourceBase/commonBase/[output format]/[input format].properties</li>
 * <li>resourceBase/commonBase/[output format]/xml.properties</li>
 * </ul>
 * <p>The properties file for the format should contain two entries:</p>
 * <ul>
 * <li>&lt;entry key="validation"&gt;path/to/schema/file&lt;/entry&gt;</li>
 * <li>&lt;entry key="transformation"&gt;path/to/xslt/file&lt;/entry&gt;</li>
 * </ul>
 * <p>Paths in the properties file are relative to the resource base url.</p>
 * <p>Currently supported formats are: DTBook and xml (heuristic block detection, no layout).</p>
 * 
 * @author Joel Håkansson, TPB
 *
 */
public class InputManager implements TaskSystem {
	//private final URL resourceBase;
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
	public InputManager(ResourceLocator localLocator, ResourceLocator commonLocator) {
		this(localLocator, commonLocator, "InputDetectorTaskSystem");
	}
	
	public InputManager(ResourceLocator localLocator, ResourceLocator commonLocator, String name) {
		//this.resourceBase = resourceBase;
		this.localLocator = localLocator;
		this.commonLocator = commonLocator;
		this.name = name;
		this.logger = Logger.getLogger(InputManager.class.getCanonicalName());
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
			if (rootNS!=null) {
				if (rootNS.equals("http://www.daisy.org/z3986/2005/dtbook/") && rootElement.equals("dtbook")) {
					inputformat = "dtbook.properties";
				} else if (rootNS.equals("http://www.daisy.org/ns/2011/obfl") && rootElement.equals("root")) {
					inputformat = "flow.properties";
				}
				// else if {
					// Add more input formats here...
				// }
			}
			// TODO: if this becomes a documented feature of the system, then a namespace should be added and the root element name changed... 
			else if (rootElement.equals("root")) {
				inputformat = "flow.properties";
			}
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

		String basePath = "config/" + outputformat + "/";

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

				HashMap h = new HashMap();
				h.putAll(p);
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

}
