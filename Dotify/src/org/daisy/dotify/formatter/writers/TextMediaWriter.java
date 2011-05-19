package org.daisy.dotify.formatter.writers;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.daisy.dotify.formatter.PagedMediaWriter;
import org.daisy.dotify.formatter.PagedMediaWriterException;
import org.daisy.dotify.formatter.SectionProperties;
import org.daisy.dotify.tools.StateObject;


/**
 * PagedMediaWriter implementation that outputs plain text.
 * @author Joel Håkansson, TPB
 *
 */
public class TextMediaWriter implements PagedMediaWriter {
	private PrintStream pst;
	//private Properties p;
	private boolean hasOpenVolume;
	private boolean hasOpenSection;
	private boolean hasOpenPage;
	/*
	private int cCols;
	private int cRows;
	private int cRowgap;
	private boolean cDuplex;*/
	private String encoding;
	private StateObject state;
	
	/**
	 * Create a new PEFMediaWriter using the supplied Properties. Available properties are:
	 * "identifier", "date"
	 * @param p configuration Properties
	 */
	public TextMediaWriter(Properties p, String encoding) {
		//this.p = p;
		hasOpenVolume = false;
		hasOpenSection = false;
		hasOpenPage = false;
		/*cCols = 0;
		cRows = 0;
		cRowgap = 0;
		cDuplex = true;*/
		this.encoding = encoding;
		this.state = new StateObject("Writer");
	}

	public void open(OutputStream os) throws PagedMediaWriterException {
		state.assertUnopened();
		state.open();
		try {
			pst = new PrintStream(os, true, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new PagedMediaWriterException("Cannot open PrintStream with " + encoding, e);
		}
		hasOpenVolume = false;		
		hasOpenSection = false;
		hasOpenPage = false;
	}

	public void newPage() {
		state.assertOpen();
		closeOpenPage();
		hasOpenPage = true;
	}

	public void newRow(CharSequence row) {
		state.assertOpen();
		pst.println(row);
	}
	
	public void newRow() {
		state.assertOpen();
		pst.println();
	}

	public void newSection(SectionProperties master) {
		state.assertOpen();
		if (!hasOpenVolume) {
			/*
			cCols = master.getPageWidth();
			cRows = master.getPageHeight();
			cRowgap = Math.round((master.getRowSpacing()-1)*4);
			cDuplex = master.duplex();*/
			hasOpenVolume = true;
		}
		closeOpenSection();
		hasOpenSection = true;
	}
	
	private void closeOpenVolume() {
		closeOpenSection();
		if (hasOpenVolume) {
			hasOpenVolume = false;
		}
	}
	
	private void closeOpenSection() {
		closeOpenPage();
		if (hasOpenSection) {
			hasOpenSection = false;
		}
	}
	
	private void closeOpenPage() {
		if (hasOpenPage) {
			hasOpenPage = false;
		}
	}

	public void close() {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		closeOpenVolume();
		pst.close();
		state.close();
	}

}
