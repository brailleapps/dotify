package  org.daisy.dotify.devtools.converters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.daisy.dotify.common.braille.BrailleNotationConverter;
import org.daisy.dotify.common.text.TextFileReader;
import org.daisy.dotify.common.text.TextFileReader.LineData;



public class BrailloTableReader {

	public BrailloTableReader() {
		
	}
	
	public void readTable(InputStream is) throws IOException {
		BrailleNotationConverter bnc = new BrailleNotationConverter("p");
		TextFileReader tfr = new TextFileReader(is, Charset.forName("windows-1252"), "\\s{1,8}", 3);
		LineData data;
		while ((data=tfr.nextLine())!=null) {
			System.out.println("Len: " + data.getFields().length);
			if (data.getFields().length<3) {
				continue;
			}
			try {
				//int dec = Integer.parseInt(data.getFields()[0]);
				String p = "p" + data.getFields()[2].replaceAll("\\s", "");
				p = bnc.parseBrailleNotation(p);
				System.out.println(data.getFields()[0] + " " + data.getFields()[1] + " " + p);
			} catch (NumberFormatException e) {  } // OK!
		}
		tfr.close();
	}
	
	public static void main(String[] args) throws IOException {
		File f = new File("c:\\BRAILLO.FIL");
		FileInputStream is = new FileInputStream(f);
		BrailloTableReader reader = new BrailloTableReader();
		reader.readTable(is);
		is.close();
	}
}
