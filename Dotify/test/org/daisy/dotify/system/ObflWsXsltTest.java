package org.daisy.dotify.system;

import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.daisy.dotify.system.SystemResourceLocator.SystemResourceIdentifier;
import org.junit.Test;
public class ObflWsXsltTest {

	@Test
	public void testWsNormalizer() throws IOException, InternalTaskException {
		
		XsltTask t = new XsltTask("WS", 
				SystemResourceLocator.getInstance().getResourceByIdentifier(SystemResourceIdentifier.OBFL_WHITESPACE_NORMALIZER_XSLT), 
				null, null);
		
		File in = File.createTempFile("TestInput", ".tmp");
		copy(this.getClass().getResourceAsStream("resource-files/ws-test-input.xml"), new FileOutputStream(in));
		
		File normalizedFile = File.createTempFile("TestResult", ".tmp");
		t.execute(in, normalizedFile);

		int ret = compareBinary(new FileInputStream(normalizedFile), this.getClass().getResourceAsStream("resource-files/ws-test-expected.xml"));
		
		if (!normalizedFile.delete()) {
			normalizedFile.deleteOnExit();
		}
		if (!in.delete()) {
			in.deleteOnExit();
		}
		
		assertTrue("Compare failed at byte: " + ret, ret==-1);
	}
	
	//Helpers
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
