package org.daisy.dotify.devtools.compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.braille.tools.FileCompare;

public class PEFFileCompareCore {
	private final static NormalizationResource def = new PackageNormalizationResource("resource-files/strip-meta.xsl");
	private final NormalizationResource nr;
	private int pos = -1;

	public PEFFileCompareCore() {
		this(def);
	}

	public PEFFileCompareCore(NormalizationResource nr) {
		this.nr = nr;
	}

	public PEFFileCompareCore(String path) {
		this(new PackageNormalizationResource(path));
	}

	public PEFFileCompareCore(final URL nr) {
		this(new URLNormalizationResource(nr));
	}

	public boolean compare(File f1, File f2) throws PEFFileCompareRunException {
		pos = -1;
		FileCompare fc = new FileCompare();

		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			factory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}

		try {
			File t1 = File.createTempFile("FileCompare", ".tmp");
			File t2 = File.createTempFile("FileCompare", ".tmp");

			try {
				StreamSource xml1 = new StreamSource(f1);
				StreamSource xml2 = new StreamSource(f2);
				Source xslt;
				Transformer transformer;

				xslt = new StreamSource(nr.getNormalizationResourceAsStream());
				transformer = factory.newTransformer(xslt);
				transformer.transform(xml1, new StreamResult(t1));

				xslt = new StreamSource(nr.getNormalizationResourceAsStream());
				transformer = factory.newTransformer(xslt);
				transformer.transform(xml2, new StreamResult(t2));
				boolean ret = fc.compareXML(new FileInputStream(t1), new FileInputStream(t2));
				pos = fc.getPos();
				return ret;
			} catch (TransformerConfigurationException e) {
				throw new PEFFileCompareRunException(e);
			} catch (TransformerException e) {
				throw new PEFFileCompareRunException(e);
			} catch (IOException e) {
				throw new PEFFileCompareRunException(e);
			} finally {
				if (!t1.delete()) {
					System.err.println("Delete failed");
					t1.deleteOnExit();
				}
				if (!t2.delete()) {
					System.err.println("Delete failed");
					t2.deleteOnExit();
				}
			}
		} catch (IOException e) {
			throw new PEFFileCompareRunException("Failed to create temp files.", e);
		}
	}

	public int getPos() {
		return pos;
	}

	static class URLNormalizationResource implements NormalizationResource {
		private final URL url;

		public URLNormalizationResource(URL url) {
			this.url = url;
		}

		@Override
		public InputStream getNormalizationResourceAsStream() {
			try {
				return url.openStream();
			} catch (IOException e) {
				return null;
			}
		}
	}

	static class PackageNormalizationResource implements NormalizationResource {
		private final String path;

		public PackageNormalizationResource(String path) {
			this.path = path;
		}

		@Override
		public InputStream getNormalizationResourceAsStream() {
			return this.getClass().getResourceAsStream(path);
		}
	}

}
