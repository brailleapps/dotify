package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.translator.BrailleFilterFactoryMakerService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import base.BrailleFilterFactoryMakerTestbase;
import osgi.config.ConfigurationOptions;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BrailleFilterFactoryMakerTest extends BrailleFilterFactoryMakerTestbase {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyTranslator(),
			junitBundles()
		);
	}

	@Inject @Filter(timeout=5000)
	BrailleFilterFactoryMakerService filterFactory;
	
	@Override
	public BrailleFilterFactoryMakerService getBrailleFilterFMS() {
		return filterFactory;
	}

}
