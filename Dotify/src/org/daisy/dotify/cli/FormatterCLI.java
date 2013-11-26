package org.daisy.dotify.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.daisy.dotify.api.engine.FormatterEngine;
import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.consumer.engine.FormatterEngineMaker;
import org.daisy.dotify.writer.PEFMediaWriter;

public class FormatterCLI {

	/**
	 * @param args
	 * @throws LayoutEngineException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, LayoutEngineException {
		if (args.length != 4) {
			System.out.println("Expected four arguments: input_file output_file locale mode");
			System.out.println(" file.obfl file.pef sv-SE uncontracted");
			System.exit(-1);
		}
		Properties p = new Properties();
		p.put("identifier", generateIdentifier());
		p.put("date", getDefaultDate("yyyy-MM-dd"));
		FormatterEngine formatter = FormatterEngineMaker.newInstance().newFormatterEngine(args[2], args[3], new PEFMediaWriter(p));
		formatter.convert(new FileInputStream(args[0]), new FileOutputStream(args[1]));
	}

	private static String generateIdentifier() {
		String id = Double.toHexString(Math.random());
		id = id.substring(id.indexOf('.') + 1);
		id = id.substring(0, id.indexOf('p'));
		return "dummy-id-" + id;
	}

	private static String getDefaultDate(String dateFormat) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(c.getTime());
	}

}
