package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
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
public class BrailleTranslatorFactoryMakerTest {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyTranslator(),
			junitBundles()
		);
	}

	@Inject @Filter(timeout=5000)
	BrailleTranslatorFactoryMakerService translatorFactory;
	
	@Test
	public void testTranslatorFactory() {
		assertNotNull(translatorFactory);
		assertTrue(translatorFactory.listSpecifications().size()>=62);
	}
	
	@Test
	public void testSwedishUncontractedTranslator() throws TranslatorConfigurationException {
		//Setup
		BrailleTranslator bt = translatorFactory.newTranslator("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		//Test
		assertNotNull(bt);
		assertEquals("⠼⠁⠃⠉", bt.translate("123").getTranslatedRemainder());
	}

	@Test
	public void testEnglishBypassTranslator() throws TranslatorConfigurationException {
		// Setup
		BrailleTranslator bt = translatorFactory.newTranslator("en", BrailleTranslatorFactory.MODE_BYPASS);
		// Test
		assertNotNull(bt);
		assertEquals("123", bt.translate("123").getTranslatedRemainder());
	}

}
