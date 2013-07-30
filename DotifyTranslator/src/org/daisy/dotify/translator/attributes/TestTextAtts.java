package org.daisy.dotify.translator.attributes;

import java.util.regex.Pattern;



public class TestTextAtts {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pattern p = Pattern.compile("\\s+");
		System.out.println(p.matcher("test av regex").find());

		DefaultTextAttribute.Builder t = new DefaultTextAttribute.Builder(10);

		DefaultTextAttribute.Builder s1 = new DefaultTextAttribute.Builder(3, "bold");
		s1.add(new DefaultTextAttribute.Builder(1, "sup").build());
		s1.add(new DefaultTextAttribute.Builder(2).build());
		t.add(s1.build());

		t.add(new DefaultTextAttribute.Builder(2).build());
		t.add(new DefaultTextAttribute.Builder(5, "i").build());

		RegexMarkerDictionary pd = new RegexMarkerDictionary.Builder().
				addPattern("\\S*\\s+\\S*", new Marker("{mb:", ":mb}"), new Marker("{sb:", ":sb}")).
				build();
		
		RegexMarkerDictionary alfaNum = new RegexMarkerDictionary.Builder().
				addPattern("[a-zA-Z0-9]+", new Marker("{sup:", ":sup}")).
				build();

		DefaultMarkerProcessor sap = new DefaultMarkerProcessor.Builder().
				addDictionary("bold", pd).
				addDictionary("sup", alfaNum).
				
				build();

		System.out.println(t.build());
		System.out.println(sap.process("1234567890", t.build()));

	}


}
