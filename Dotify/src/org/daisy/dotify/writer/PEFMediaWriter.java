package org.daisy.dotify.writer;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.formatter.SectionProperties;
import org.daisy.dotify.tools.StateObject;


/**
 * PagedMediaWriter implementation that outputs PEF 2008-1.
 * @author Joel Håkansson
 *
 */
public class PEFMediaWriter implements PagedMediaWriter {
	private final static String DC_NAMESPACE_URI = "http://purl.org/dc/elements/1.1/";
	private final Pattern nonBraillePattern;
	private PrintStream pst;
	private Properties p;
	private boolean hasOpenVolume;
	private boolean hasOpenSection;
	private boolean hasOpenPage;
	private int cCols;
	private int cRows;
	private int cRowgap;
	private boolean cDuplex;
	private StateObject state;
	private int errorCount = 0;
	
	/**
	 * Create a new PEFMediaWriter using the supplied Properties. Available properties are:
	 * "identifier", "date"
	 * @param p configuration Properties
	 */
	public PEFMediaWriter(Properties p) {
		this.p = p;
		hasOpenVolume = false;
		hasOpenSection = false;
		hasOpenPage = false;
		cCols = 0;
		cRows = 0;
		cRowgap = 0;
		cDuplex = true;
		state = new StateObject("Writer");
		this.nonBraillePattern = Pattern.compile("[^\u2800-\u28FF]+");
	}

	public void open(OutputStream os, List<MetaDataItem> meta) throws PagedMediaWriterException {
		state.assertUnopened();
		state.open();
		try {
			pst = new PrintStream(os, true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen
			throw new PagedMediaWriterException("Cannot open PrintStream with UTF-8.", e);
		}
		hasOpenVolume = false;		
		hasOpenSection = false;
		hasOpenPage = false;
		pst.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pst.println("<pef version=\"2008-1\" xmlns=\"http://www.daisy.org/ns/2008/pef\">");
		pst.println("<head>");
		pst.println("<meta xmlns:dc=\"" + DC_NAMESPACE_URI + "\" " +
				    "xmlns:generator=\"http://daisymfc.svn.sourceforge.net/viewvc/daisymfc/trunk/dmfc/transformers/org_pef_dtbook2pef/\"" +
				">");
		pst.println("<dc:format>application/x-pef+xml</dc:format>");
		// these could be moved to OBFL-input
		pst.println("<dc:identifier>" + p.getProperty(SystemKeys.IDENTIFIER, "identifier?") + "</dc:identifier>");
		pst.println("<dc:date>" + p.getProperty(SystemKeys.DATE, "date?") + "</dc:date>");

		if (meta!=null) {
			for (MetaDataItem item : meta) {
				if (item.getKey().getNamespaceURI().equals(DC_NAMESPACE_URI)) {
					if (!(item.getKey().getLocalPart().equals("format") && item.getKey().getLocalPart().equals("identifier") && item.getKey().getLocalPart().equals("date"))) {
						Logger.getLogger(this.getClass().getCanonicalName()).fine("adding metadata " + item.getKey() + " " + item.getValue());
						pst.println("<dc:" + item.getKey().getLocalPart() + ">" + escape(item.getValue()) + "</dc:" + item.getKey().getLocalPart() + ">");
					}
				}
			}
		}
		for (Object key : p.keySet()) {
			pst.println("<generator:entry key=\"" + key + "\">" + p.get(key) + "</generator:entry>" );
		}
		pst.println("</meta>");
		pst.println("</head>");
		pst.println("<body>");
	}

	public void newPage() {
		state.assertOpen();
		closeOpenPage();
		if (!hasOpenSection) {
			throw new IllegalStateException("No open section.");
		}
		pst.println("<page>");
		hasOpenPage = true;
	}

	public void newRow(CharSequence row) {
		state.assertOpen();
		if (nonBraillePattern.matcher(row).matches()) {
			if (errorCount<10) {
				Logger.getLogger(this.getClass().getCanonicalName()).fine(
						"Non-braille characters in output"+
							(errorCount==9?" (supressing additional messages of this kind)":"") + ": " + row
						);
				errorCount++;
			}
		}
		pst.println("<row>"+row+"</row>");
	}
	
	public void newRow() {
		state.assertOpen();
		pst.println("<row/>");
	}
	
	public void newVolume(SectionProperties master) {
		state.assertOpen();
		closeOpenVolume();
		cCols = master.getPageWidth();
		cRows = master.getPageHeight();
		cRowgap = Math.round((master.getRowSpacing()-1)*4);
		cDuplex = master.duplex();
		pst.println("<volume cols=\"" + cCols + 
				"\" rows=\"" + cRows +
				"\" rowgap=\"" + cRowgap +
				"\" duplex=\"" + cDuplex +
				"\">");
		hasOpenVolume = true;
	}

	public void newSection(SectionProperties master) {
		state.assertOpen();
		if (!hasOpenVolume) {
			newVolume(master);
		}
		closeOpenSection();
		pst.print("<section");

		if (cCols!=master.getPageWidth()) {
			pst.print(" cols=\"" + master.getPageWidth() + "\"");
		}
		if (cRows!=master.getPageHeight()) { 
			pst.print(" rows=\"" + master.getPageHeight() + "\"");
		}
		if (cRowgap!=Math.round((master.getRowSpacing()-1)*4)) {
			pst.print(" rowgap=\"" + Math.round((master.getRowSpacing()-1)*4) + "\"");
		}
		if (cDuplex!=master.duplex()) {
			pst.print(" duplex=\"" + master.duplex() + "\"");
		}
		pst.println(">");
		hasOpenSection = true;
	}
	
	private String escape(String text) {
		if (text == null) {
			return "";
		}
		return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
	}

	private void closeOpenVolume() {
		state.assertOpen();
		closeOpenSection();
		if (hasOpenVolume) {
			pst.println("</volume>");
			hasOpenVolume = false;
		}
	}
	
	private void closeOpenSection() {
		state.assertOpen();
		closeOpenPage();
		if (hasOpenSection) {
			pst.println("</section>");
			hasOpenSection = false;
		}
	}
	
	private void closeOpenPage() {
		state.assertOpen();
		if (hasOpenPage) {
			pst.println("</page>");
			hasOpenPage = false;
		}
	}

	public void close() {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		closeOpenVolume();
		pst.println("</body>");
		pst.println("</pef>");
		pst.close();
		state.close();
	}

}
