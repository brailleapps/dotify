package org.daisy.dotify.impl.input.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.tasks.ExpandingTask;
import org.daisy.dotify.api.tasks.InternalTask;
import org.daisy.dotify.api.tasks.InternalTaskException;
import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskSystemException;
import org.daisy.dotify.common.io.ResourceLocator;
import org.daisy.dotify.common.io.ResourceLocatorException;
import org.daisy.dotify.common.xml.XMLInfo;
import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.dotify.common.xml.XMLToolsException;
import org.daisy.dotify.impl.input.Keys;
import org.daisy.dotify.impl.input.ValidatorTask;
import org.daisy.dotify.impl.input.XsltTask;

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
 * @author Joel Håkansson
 *
 */
public class XMLInputManager implements TaskGroup {
	private final static String TEMPLATES_PATH = "templates/";
	private final static String LOCALIZATION_PROPS = "localization.xml";
	private final ResourceLocator localLocator;
	private final ResourceLocator commonLocator;
	private final String name;
	private final Logger logger;

	/**
	 * Create a new InputDetectorTaskSystem. 
	 * @param localLocator a locator for local resources
	 * @param commonLocator a locator for common resources
	 */
	public XMLInputManager(ResourceLocator localLocator, ResourceLocator commonLocator) {
		this(localLocator, commonLocator, "XMLInputManager");
	}
	
	public XMLInputManager(ResourceLocator localLocator, ResourceLocator commonLocator, String name) {
		this.localLocator = localLocator;
		this.commonLocator = commonLocator;
		this.name = name;
		this.logger = Logger.getLogger(XMLInputManager.class.getCanonicalName());
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters)
			throws TaskSystemException {
		String template;
		if (parameters.get(Keys.TEMPLATE)==null) {
			logger.info("No template set, using default.");
			template = "default";
		} else {
			template = parameters.get(Keys.TEMPLATE).toString().toLowerCase();
		}
		
		List<InternalTask> ret = new ArrayList<>();
		ret.add(new XMLExpandingTask(template, makeXSLTParams(parameters)));
		
		return ret;
	}
	
	private Map<String, Object> makeXSLTParams(Map<String, Object> parameters) {
		Map<String, Object> xsltParams = new HashMap<>();
		for (String key2 : parameters.keySet()) {
			xsltParams.put(key2, parameters.get(key2));
		}
		{
			Properties p2 = new Properties();
			try {
				p2.loadFromXML(localLocator.getResource(LOCALIZATION_PROPS).openStream());
			} catch (InvalidPropertiesFormatException e) {
				logger.log(Level.FINE, "", e);
			} catch (ResourceLocatorException e) {
				logger.log(Level.FINE, "", e);
			} catch (IOException e) {
				logger.log(Level.FINE, "", e);
			}
			
			for (Object key3 : p2.keySet()) {
				xsltParams.put(key3.toString(), p2.get(key3).toString());
			}
		}
		return Collections.unmodifiableMap(xsltParams);
	}
	
	private class XMLExpandingTask extends ExpandingTask {
		private final String template;
		private final Map<String, Object> xsltParams;
		
		XMLExpandingTask(String template, Map<String, Object> xsltParams) {
			super("XML Tasks Bundle");
			this.template = template;
			this.xsltParams = xsltParams;
		}

		@Override
		public List<InternalTask> resolve(File input) throws InternalTaskException {
			//String input = parameters.get(Keys.INPUT).toString();
			String inputformat = null;
			try {
				XMLInfo peekResult = XMLTools.parseXML(input, true);
				String rootNS = peekResult.getUri();
				String rootElement = peekResult.getLocalName();
				DefaultInputUrlResourceLocator p = DefaultInputUrlResourceLocator.getInstance();

				inputformat = p.getConfigFileName(rootElement, rootNS);
				if (inputformat !=null && "".equals(inputformat)) {
					return new ArrayList<>();
				}
			} catch (XMLToolsException e) {
				throw new InternalTaskException("XMLToolsException while reading input", e);
			} catch (IOException e) {
				throw new InternalTaskException("IOException while reading input", e);
			}
			
			String xmlformat = "xml.properties";
			String basePath = TEMPLATES_PATH + template + "/";
			if (inputformat!=null) {
				try {
					return readConfiguration(localLocator, basePath + inputformat);
				} catch (ResourceLocatorException e) {
					logger.fine("Cannot find localized URL " + basePath + inputformat);
				}
			}
			try {
				return readConfiguration(localLocator, basePath + xmlformat);
			} catch (ResourceLocatorException e) {
				logger.fine("Cannot find localized URL " + basePath + xmlformat);
			}
			if (inputformat!=null) {
				try {
					return readConfiguration(commonLocator, basePath + inputformat);
				} catch (ResourceLocatorException e) {
					logger.fine("Cannot find common URL " + basePath + inputformat);
				}
			}
			try {
				return readConfiguration(commonLocator, basePath + xmlformat);
			} catch (ResourceLocatorException e) {
				logger.fine("Cannot find common URL " + basePath + xmlformat);
			}
			throw new InternalTaskException("Unable to open a configuration stream for the format.");
		}
		
		private ArrayList<InternalTask> readConfiguration(ResourceLocator locator, String path) throws InternalTaskException, ResourceLocatorException {
			URL t = locator.getResource(path);
			ArrayList<InternalTask> setup = new ArrayList<>();

			try (InputStream propsStream = t.openStream()) {
				logger.fine("Opening stream: " + t.getFile());
				try {
					//if (propsStream != null) {
						Properties p = new Properties();
						p.loadFromXML(propsStream);
						propsStream.close();
	
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
										setup.add(new XsltTask("XML to OBFL converter: " + s, locator.getResource(s), xsltParams));
									}
								}
							} else {
								logger.info("Unrecognized key: " + key);
							}
						}
					/*} else {
						throw new InternalTaskException("Unable to open a configuration stream for the format.");
					}*/
				} catch (InvalidPropertiesFormatException e) {
					throw new InternalTaskException("Unable to read settings file.", e);
				} catch (IOException e) {
					throw new InternalTaskException("Unable to open settings file.", e);
				}
			} catch (IOException e) {
				logger.log(Level.FINE, "Cannot open stream: " + t.getFile(), e);
				throw new ResourceLocatorException("Cannot open stream");
			}
			return setup;
		}
		
	}

}
