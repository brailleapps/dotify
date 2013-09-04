package se.mtm.common.xml;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlTools {

	public static void transform(File input, File output, InputStream xsltFile, Map<String, Object> params) {
		try {
			StreamSource xslt = new StreamSource(xsltFile);

			Transformer transformer = TransformerFactory.newInstance().newTransformer(xslt);
			for (String name : params.keySet()) {
				transformer.setParameter(name, params.get(name));
			}

			StreamSource source = new StreamSource(input);
			StreamResult result = new StreamResult(output);

			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
