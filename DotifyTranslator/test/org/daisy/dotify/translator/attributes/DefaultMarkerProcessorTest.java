package org.daisy.dotify.translator.attributes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultMarkerProcessorTest {
	private final DefaultMarkerProcessor sap;
	
	public DefaultMarkerProcessorTest() {
		RegexMarkerDictionary boldDefinition = new RegexMarkerDictionary.Builder().
				addPattern("\\s+", new Marker("{mb:", ":mb}"), new Marker("{sb:", ":sb}")).
				build();
		
		RegexMarkerDictionary italicDefinition = new RegexMarkerDictionary.Builder().
				addPattern("\\s+", new Marker("{mi:", ":mi}"), new Marker("{si:", ":si}")).
				build();
		
		RegexMarkerDictionary supDefinition = new RegexMarkerDictionary.Builder().
				addPattern("\\A[a-zA-Z0-9]+\\z", new Marker("{sup:", ":sup}")).
				build();

		RegexMarkerDictionary subDefinition = new RegexMarkerDictionary.Builder().
				addPattern("\\A[a-zA-Z0-9]+\\z", new Marker("{sub:", ":sub}")).
				build();

		sap = new DefaultMarkerProcessor.Builder().
				addDictionary("b", boldDefinition).
				addDictionary("i", italicDefinition).
				addDictionary("sup", supDefinition).
				addDictionary("sub", subDefinition).
				build();
	}
	
	@Test
	public void test_01() {
		DefaultTextAttribute.Builder t = new DefaultTextAttribute.Builder(10);

		DefaultTextAttribute.Builder s1 = new DefaultTextAttribute.Builder(3, "b");
		s1.add(new DefaultTextAttribute.Builder(1, "sup").build());
		s1.add(new DefaultTextAttribute.Builder(2).build());
		t.add(s1.build());

		t.add(new DefaultTextAttribute.Builder(2).build());
		t.add(new DefaultTextAttribute.Builder(5, "i").build());

		String actual = sap.process("1234567890", t.build());
		assertEquals("Tests a single string of digits", "{sb:{sup:1:sup}23:sb}45{si:67890:si}", actual);
	}

	@Test
	public void test_02() {
		DefaultTextAttribute.Builder t = new DefaultTextAttribute.Builder(10, "i");

		DefaultTextAttribute.Builder s1 = new DefaultTextAttribute.Builder(3, "b");
		s1.add(new DefaultTextAttribute.Builder(1, "sup").build());
		s1.add(new DefaultTextAttribute.Builder(2).build());
		t.add(s1.build());

		t.add(new DefaultTextAttribute.Builder(2).build());
		t.add(new DefaultTextAttribute.Builder(5, "i").build());

		String actual = sap.process("1234567890", t.build());
		assertEquals("Tests a single string of digits", "{si:{sb:{sup:1:sup}23:sb}45{si:67890:si}:si}", actual);
	}

	@Test
	public void test_03() {
		String input = "Test of a multi-word attribution.";
		DefaultTextAttribute.Builder t = new DefaultTextAttribute.Builder(input.length());
		t.add(new DefaultTextAttribute.Builder(5).build());
		t.add(new DefaultTextAttribute.Builder(4, "b").build());
		t.add(new DefaultTextAttribute.Builder(1).build());
		t.add(new DefaultTextAttribute.Builder(22, "i").build());
		t.add(new DefaultTextAttribute.Builder(1).build());
		String actual = sap.process(input, t.build());
		assertEquals("", "Test {mb:of a:mb} {mi:multi-word attribution:mi}.", actual);
	}

}
