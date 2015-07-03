package osgi;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.daisy.dotify.api.cr.TaskSystemFactoryMakerService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import osgi.config.ConfigurationOptions;
import base.TaskSystemFactoryMakerTestbase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TaskSystemFactoryMakerTest extends TaskSystemFactoryMakerTestbase {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyTasks(),
			ConfigurationOptions.dotifyFormatter(),
			junitBundles()
		);
	}

	@Inject @Filter(timeout=5000)
	TaskSystemFactoryMakerService factory;

	@Override
	public TaskSystemFactoryMakerService getTaskSystemFMS() {
		return factory;
	}
}
