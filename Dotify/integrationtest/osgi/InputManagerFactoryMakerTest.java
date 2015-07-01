package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.Set;

import javax.inject.Inject;

import org.daisy.dotify.api.cr.InputManagerFactoryMakerService;
import org.daisy.dotify.common.text.FilterLocale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class InputManagerFactoryMakerTest {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyTasks(),
			junitBundles()
		);
	}

	@Inject @Filter(timeout=5000)
	InputManagerFactoryMakerService factory;
	
	@Test
	public void testFactoryExists() {
		//Test
		assertNotNull("Factory exists.", factory);
	}
	
	@Test
	public void testSupportedFileFormats() {
		Set<String> formats = factory.listSupportedFileFormats();
		assertEquals(5, formats.size());
		assertTrue(formats.contains("dtbook"));
		assertTrue(formats.contains("text"));
		assertTrue(formats.contains("xml"));
		assertTrue(formats.contains("txt"));
		assertTrue(formats.contains("obfl"));
	}
	
	@Test
	public void testSupportedLocales() {
		Set<String> locales = factory.listSupportedLocales();
		assertEquals(2, locales.size());
		assertTrue(locales.contains("sv-SE"));
		assertTrue(locales.contains("en-US"));
	}
	
	@Test
	public void testGetFactoryForSwedish() {
		//Setup
		FilterLocale locale = FilterLocale.parse("sv-SE");
		
		//Test
		assertNotNull(factory.getFactory(locale.toString(), "xml"));
		assertNotNull(factory.getFactory(locale.toString(), "text"));
		assertNotNull(factory.getFactory(locale.toString(), "obfl"));
		assertNotNull(factory.getFactory(locale.toString(), "txt"));
		assertNotNull(factory.getFactory(locale.toString(), "dtbook"));
	}
	
	@Test
	public void testGetFactoryForEnglish() {
		//Setup
		FilterLocale locale = FilterLocale.parse("en-US");
		
		//Test
		assertNotNull(factory.getFactory(locale.toString(), "xml"));
		assertNotNull(factory.getFactory(locale.toString(), "text"));
		assertNotNull(factory.getFactory(locale.toString(), "obfl"));
		assertNotNull(factory.getFactory(locale.toString(), "txt"));
		assertNotNull(factory.getFactory(locale.toString(), "dtbook"));
	}
}
