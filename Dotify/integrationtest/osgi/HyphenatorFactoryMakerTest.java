package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
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
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class HyphenatorFactoryMakerTest {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyHyphenator(),
			junitBundles()
		);
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

}
