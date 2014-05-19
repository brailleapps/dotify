package org.daisy.dotify.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.daisy.dotify.api.engine.FormatterEngine;
import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.MediaTypes;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.daisy.dotify.api.writer.PagedMediaWriterFactory;
import org.daisy.dotify.consumer.engine.FormatterEngineMaker;
import org.daisy.dotify.consumer.writer.PagedMediaWriterFactoryMaker;

public class FormatterCLI {

	/**
	 * @param args
	 * @throws LayoutEngineException
	 * @throws FileNotFoundException
	 * @throws PagedMediaWriterConfigurationException 
	 */
	public static void main(String[] args) throws FileNotFoundException, LayoutEngineException, PagedMediaWriterConfigurationException {
		if (args.length != 4) {
			System.out.println("Expected four arguments: input_file output_file locale mode");
			System.out.println(" file.obfl file.pef sv-SE uncontracted");
			System.exit(-1);
		}
		PagedMediaWriterFactory f = PagedMediaWriterFactoryMaker.newInstance().getFactory(MediaTypes.PEF_MEDIA_TYPE);

		f.setFeature("identifier", generateIdentifier());
		f.setFeature("date", getDefaultDate("yyyy-MM-dd"));
		
		FormatterEngine formatter = FormatterEngineMaker.newInstance().newFormatterEngine(args[2], args[3], f.newPagedMediaWriter());
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
