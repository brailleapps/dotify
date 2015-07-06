package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;
import base.BrailleTranslatorFactoryMakerTestbase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BrailleTranslatorFactoryMakerTest extends BrailleTranslatorFactoryMakerTestbase {

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
	
	public BrailleTranslatorFactoryMakerService getBrailleTranslatorFMS() {
		return translatorFactory;
	}

}
