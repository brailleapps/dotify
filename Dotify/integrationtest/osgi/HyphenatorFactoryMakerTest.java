package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;
import base.HyphenatorFactoryMakerTestbase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class HyphenatorFactoryMakerTest extends HyphenatorFactoryMakerTestbase {

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

	@Override
	public HyphenatorFactoryMakerService getHyphenatorFMS() {
		return hyphenatorFactory;
	}

}
