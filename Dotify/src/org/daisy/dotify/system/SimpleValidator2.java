/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.dotify.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.daisy.util.xml.sax.SAXParseExceptionMessageFormatter;
import org.daisy.util.xml.validation.ValidationException;
import org.daisy.util.xml.validation.ValidationUtils;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import se.mtm.common.io.InputStreamMaker;

/**
 * A simple validator using the <code>org.daisy.util.xml.validation.jaxp</code> APIs.
 * <p>An instance of SimpleValidator can be reused to validate multiple files.</p>
 * @author Linus Ericson
 * @author Markus Gylling
 */
public class SimpleValidator2 implements ErrorHandler {

	private Map<Source, String> mSchemaSources = null;
	private ErrorHandler mErrorHandler = this;
	private EntityResolver mResolver = null;
	private LSResourceResolver mLSResolver = null;
	private Set<Validator> mJaxpValidators = null;
	
	/**
	 * Constructor. Use this to validate resources against inline schemas and additional schema resources given as param.
	 * @param schemas A collection of schema identifiers 
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator2(Collection<String> schemas, ErrorHandler handler) throws SAXException, TransformerException {
		mSchemaSources = new HashMap<Source, String>();
		mErrorHandler = handler;
		mResolver = CatalogEntityResolver.getInstance();
		mLSResolver = CatalogEntityResolver.getInstance();
		// Loop through supplied schemas
		for (String schema : schemas) {
			Map<Source,String> map = ValidationUtils.toSchemaSources(schema);
			mSchemaSources.putAll(map);
		}
	}

	
	/**
	 * Constructor. Use this to validate resources against inline schemas and additional schema resources given as param.
	 * @param schemas A collection of schema URLs 
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator2(ErrorHandler handler, Collection<URL> schemas) throws SAXException, TransformerException {
		mSchemaSources = new HashMap<Source, String>();
		mErrorHandler = handler;
		mResolver = CatalogEntityResolver.getInstance();
		mLSResolver = CatalogEntityResolver.getInstance();
		// Loop through supplied schemas
		for (URL schema : schemas) {
			Map<Source,String> map = ValidationUtils.toSchemaSources(schema);
			mSchemaSources.putAll(map);
		}
	}
	
	/**
	 * Constructor. Use this to validate resources against inline schemas and an additional schema resource given as param.
	 * @param schema A schema identifier (PID, SID, pathspec, URL)
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator2(String schema, ErrorHandler handler) throws SAXException, TransformerException {		
		this(toCollection(schema), handler);
	}

	/**
	 * Constructor. Use this to validate resources against inline schemas and an additional schema resource given as param.
	 * @param schema A schema identifier (PID, SID, pathspec, URL)
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator2(URL schema, ErrorHandler handler) throws SAXException, TransformerException {		
		this(handler, toCollection(schema));
	}
	
	/**
	 * Constructor. Use this to validate resources against inline schemas only.
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator2(ErrorHandler handler) throws SAXException {		
		mErrorHandler = handler;
		mResolver = CatalogEntityResolver.getInstance();
		mLSResolver = CatalogEntityResolver.getInstance();
	}
	
	public boolean validate(InputStreamMaker url) throws ValidationException {
		return validate(url, null);
	}
	/**
	 * Validate a resource.
	 * <p>Validation will include inline DTD if available, and any schemas set in constructor.</p>
	 * <p>Note - inline XSDs must be set using constructor as well.</p>
	 * <p>Note - a return of true does not indicate validity: implement ErrorHandler to determine this.</p>
	 * @return true if validation was performed without exceptions. 	 
	 */
	public boolean validate(InputStreamMaker url, String systemId) throws ValidationException {
		boolean jaxpResult = true;
		boolean dtdResult = true;
		PeekResult inputFilePeekResult;	
		Peeker peeker = null;
		
		try {
			try {
				peeker = PeekerPool.getInstance().acquire();
				inputFilePeekResult = peeker.peek(url.newInputStream());
			} finally{
				PeekerPool.getInstance().release(peeker);
			}
				
			// Do DTD validation?
			if ((inputFilePeekResult != null) 
					&& (inputFilePeekResult.getPrologSystemId()!=null
							||inputFilePeekResult.getPrologPublicId()!=null)){
					dtdResult = doSAXDTDValidation(url.newInputStream());	
			}
								
			// Apply schemas set in constructor	
			jaxpResult = doJAXPSchemaValidation(url.newInputStream(), systemId);
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new ValidationException(e.getMessage(),e);
		} 
		
		return jaxpResult && dtdResult;
	}
		
	
	/**
	 * Run a SAXParse with DTD validation turned on. 
	 * @throws SAXException  
	 */
	private boolean doSAXDTDValidation(InputStream url) throws SAXException {
		boolean result = true;
    	Map<String, Object> features = new HashMap<String, Object>();
    	SAXParser saxParser = null;
	    try{
	    	features.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
	    	features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.TRUE);        	
	    	saxParser = SAXParserPool.getInstance().acquire(features,null);
	    	saxParser.getXMLReader().setErrorHandler(mErrorHandler);    	
	    	saxParser.getXMLReader().setContentHandler(new DefaultHandler());
	    	saxParser.getXMLReader().setEntityResolver(mResolver);
	    	saxParser.getXMLReader().parse(new InputSource(url));	    	
		}catch (Exception e) {						
			this.fatalError(e);			
			result = false;
		}finally{
			try {
				SAXParserPool.getInstance().release(saxParser,features,null);
			} catch (PoolException e) {

			}
		}
		return result;
	}
	
	/**
	 * Attempt to validate the input file using javax.xml.validation against a set of schema Sources
	 * @throws SAXException 
	 */
	private boolean doJAXPSchemaValidation(InputStream url, String systemId) throws SAXException {
		boolean result = true;		
		if(mSchemaSources==null||mSchemaSources.isEmpty()) return result;
		
		if(mJaxpValidators==null) {
			//this is the first validate call on this instance
			//create the validators	set
			mJaxpValidators = new HashSet<Validator>();
			HashMap<String,SchemaFactory> factoryMap = new HashMap<String,SchemaFactory>();		//cache to not create multiple identical factories     	     	 
	    	SchemaFactory anySchemaFactory = null;												//Schema language neutral jaxp.validation driver

	    	for (Source source : mSchemaSources.keySet()) {
				try{				
					String schemaNsURI = mSchemaSources.get(source);				
					if(!factoryMap.containsKey(schemaNsURI)) {
						factoryMap.put(schemaNsURI,SchemaFactory.newInstance(schemaNsURI));
					}
					anySchemaFactory = factoryMap.get(schemaNsURI);
					anySchemaFactory.setErrorHandler(mErrorHandler);
					anySchemaFactory.setResourceResolver(mLSResolver);
					Schema schema = anySchemaFactory.newSchema(source);													
					Validator jaxpValidator = schema.newValidator();	
					mJaxpValidators.add(jaxpValidator);
				} catch (Exception e) {
					this.fatalError(e);
					result = false;				
				}        
			}			
		}
		
		for (Validator validator : mJaxpValidators) {
			try{
				StreamSource ss = new StreamSource(url);
				ss.setSystemId(systemId);
				validator.validate(ss);
				if(ss.getInputStream()!=null) ss.getInputStream().close();
			} catch (Exception e) {
				this.fatalError(e);
				result = false;				
			}
		}
		
    	return result;
	}
		
	/**
	 * Set an LSResolver. If this method not called, the SimpleValidator instance defaults to CatalogEntityResolver
	 */
	public void setResolver(LSResourceResolver resolver) {
		this.mLSResolver = resolver;
	}

	/**
	 * Set an EntityResolver. If this method not called, the SimpleValidator instance defaults to CatalogEntityResolver
	 */
	public void setResolver(EntityResolver resolver) {
		this.mResolver = resolver;
	}
	
	private static Collection<String> toCollection(String str) {
		Collection<String> coll = new ArrayList<String>();
		coll.add(str);
		return coll;
	}

	private static Collection<URL> toCollection(URL url) {
		Collection<URL> coll = new ArrayList<URL>();
		coll.add(url);
		return coll;
	}


	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	@SuppressWarnings("unused")
	public void error(SAXParseException exception) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Error ", exception));		
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@SuppressWarnings("unused")
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Fatal error ", exception));		
	}

	
	private void fatalError(Exception e) throws SAXException {
		SAXParseException spe;
		if(e instanceof SAXParseException) {
			spe = (SAXParseException)e;
		}else{
			spe = new SAXParseException(e.getMessage(),null,e); 
		}
		this.mErrorHandler.fatalError(spe);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	@SuppressWarnings("unused")
	public void warning(SAXParseException exception) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Warning ", exception));		
	}
	
//	// Has inline XSDs?
//	if (inputFilePeekResult!=null){
//		Set<String> xsis = inputFilePeekResult.getXSISchemaLocationURIs();
//		for (String str : xsis) {			
//			Map<Source,String> map = ValidationUtils.toSchemaSources(str);					
//			mSchemaSources.putAll(map);
//		}
//	}

}