package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.daisy.dotify.api.text.Integer2Text;
import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;
import org.daisy.dotify.api.text.IntegerOutOfRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.util.Filter;

@RunWith(PaxExam.class)
public class OsgiIntegTest {

	@Configuration 
	public Option[] configure() {
		return options(
			mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.scr").version("1.6.2"),
			mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.api").version("1.0.0"),
			mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.common").version("1.0.0"),
			mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.text.impl").version("1.0.0"),
			mavenBundle().groupId("com.googlecode.texhyphj").artifactId("texhyphj").version("1.2"),
			mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.hyphenator.impl").version("1.0.0"),
			//mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.formatter.impl").version("1.0-SNAPSHOT"),
			junitBundles()
		);
	}
	
	@Inject @Filter(timeout=5000)
	Integer2TextFactoryMakerService int2textFactory;

	@Test
	public void testInt2TextFactory() throws Integer2TextConfigurationException, IntegerOutOfRange {
		assertNotNull(int2textFactory);
		assertTrue(int2textFactory.listLocales().size()>=3);
	}
	
	@Test
	public void testInt2Text() throws Integer2TextConfigurationException, IntegerOutOfRange {
		assertNotNull(int2textFactory);
		Integer2Text en = int2textFactory.newInteger2Text("en");
		assertEquals("two", en.intToText(2));
	}

	@Inject @Filter(timeout=5000)
	HyphenatorFactoryMakerService hyphenatorFactory;
	
	@Test
	public void testHyphenatorFactory() {
		assertNotNull(hyphenatorFactory);
		assertTrue(hyphenatorFactory.listLocales().size()>=61);
	}
	
	@Test
	public void testHyphenator() throws HyphenatorConfigurationException {
		assertNotNull(hyphenatorFactory);
		HyphenatorInterface h = hyphenatorFactory.newHyphenator("en");
		assertEquals("hy­phen­a­tion", h.hyphenate("hyphenation"));
	}

	/*
	@Inject @Filter(timeout=5000)
	BrailleTranslatorFactoryMakerService translatorFactory;
	
	@Test
	public void testTranslatorFactory() {
		assertNotNull(translatorFactory);
		assertTrue(translatorFactory.listSpecifications().size()>=1);
	}*/

}
