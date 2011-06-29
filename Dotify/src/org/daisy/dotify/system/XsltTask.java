package org.daisy.dotify.system;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;

/**
 * <p>Task that runs an XSLT conversion.</p>
 * <p>Input file type requirement: XML</p>
 * 
 * @author  Joel Hakansson
 * @version 4 maj 2009
 * @since 1.0
 */
public class XsltTask extends InternalTask {
	final URL url;
	final String factory;
	final Map<String, Object> options;
	
	/**
	 * Create a new XSLT task.
	 * @param name task name
	 * @param url relative path to XSLT
	 * @param factory XSLT factory to use
	 * @param options XSLT parameters
	 */
	public XsltTask(String name, URL url, String factory, Map<String, Object> options) {
		super(name);
		this.url = url;
		this.factory = factory;
		this.options = options;
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			Stylesheet.apply(input.getAbsolutePath(), url, output.getAbsolutePath(), factory, options, null);
		} catch (XSLTException e) {
			throw new InternalTaskException("Error: ", e);
		}

	}

}
