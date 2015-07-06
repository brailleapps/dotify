package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;
import base.Integer2TextFactoryTestbase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Integer2TextFactoryTest extends Integer2TextFactoryTestbase {

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

	@Override
	public Integer2TextFactoryMakerService getInteger2TextFMS() {
		return int2textFactory;
	}

}
