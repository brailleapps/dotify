package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMakerService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;
import base.PagedMediaWriterFactoryMakerTestbase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class PagedMediaWriterFactoryMakerTest extends PagedMediaWriterFactoryMakerTestbase {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyFormatter(),
			junitBundles()
		);
	}

	@Inject @Filter(timeout=5000)
	PagedMediaWriterFactoryMakerService pagedMediaFactory;

	@Override
	public PagedMediaWriterFactoryMakerService getPageMedaWriterFMS() {
		return pagedMediaFactory;
	}

}
