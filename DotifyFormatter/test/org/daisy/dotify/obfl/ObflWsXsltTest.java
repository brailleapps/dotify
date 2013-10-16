package org.daisy.dotify.obfl;

import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
public class ObflWsXsltTest {

	@Test
	public void testWsNormalizer_01() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-01.xml", "resource-files/ws-test-expected-01.xml");
		assertTrue("Compare (Toc) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_02() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-02.xml", "resource-files/ws-test-expected-02.xml");
		assertTrue("Compare (Block) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_03() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-03.xml", "resource-files/ws-test-expected-03.xml");
		assertTrue("Compare (Span) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_04() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-04.xml", "resource-files/ws-test-expected-04.xml");
		assertTrue("Compare (Line breaks) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_05() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-05.xml", "resource-files/ws-test-expected-05.xml");
		assertTrue("Compare (Leader) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_06() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-06.xml", "resource-files/ws-test-expected-06.xml");
		assertTrue("Compare (Evaluate) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_07() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-07.xml", "resource-files/ws-test-expected-07.xml");
		assertTrue("Compare (Page number) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_08() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-08.xml", "resource-files/ws-test-expected-08.xml");
		assertTrue("Compare (Marker) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_09() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-09.xml", "resource-files/ws-test-expected-09.xml");
		assertTrue("Compare (Anchor) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_10() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-10.xml", "resource-files/ws-test-expected-10.xml");
		assertTrue("Compare (Style) failed at byte: " + ret, ret == -1);
	}

	@Test
	public void testWsNormalizer_11() throws IOException, XMLStreamException {
		int ret = testWsNormalizer("resource-files/ws-test-input-11.xml", "resource-files/ws-test-expected-11.xml");
		assertTrue("Compare (NBSP) failed at byte: " + ret, ret == -1);
	}

	// Helpers
	public int testWsNormalizer(String input, String expected) throws IOException, XMLStreamException {
		
		File in = File.createTempFile("TestInput", ".tmp");
		copy(this.getClass().getResourceAsStream(input), new FileOutputStream(in));
		
		File normalizedFile = File.createTempFile("TestResult", ".tmp");
		OBFLWsNormalizer t = new OBFLWsNormalizer(new FileInputStream(in), new FileOutputStream(normalizedFile));
		t.parse();
		int ret = compareBinary(new FileInputStream(normalizedFile), this.getClass().getResourceAsStream(expected));
		
		if (!normalizedFile.delete()) {
			normalizedFile.deleteOnExit();
		}
		if (!in.delete()) {
			in.deleteOnExit();
		}
		
		return ret;
	}

	public static void copy(InputStream is, OutputStream os) throws IOException {
		InputStream bis = new BufferedInputStream(is);
		OutputStream bos = new BufferedOutputStream(os);
		int b;
		while ((b = bis.read())!=-1) {
			bos.write(b);
		}
		bos.flush();
		bos.close();
		bis.close();
	}
	
	public int compareBinary(InputStream f1, InputStream f2) throws IOException {
		InputStream bf1 = new BufferedInputStream(f1);
		InputStream bf2 = new BufferedInputStream(f2);
		int pos = 0;
		try {
			int b1;
			int b2;
			while ((b1 = bf1.read())!=-1 & b1 == (b2 = bf2.read())) {
				pos++;
				//continue
			}
			if (b1!=-1 || b2!=-1) {
				return pos;
			}
			return -1;
		} finally {
			bf1.close();
			bf2.close();
		}
	}

}
