package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

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
public class Integer2TextFactoryTest {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyText(),
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

}
