package osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.Set;

import javax.inject.Inject;

import org.daisy.dotify.api.cr.TaskGroupFactoryMakerService;
import org.daisy.dotify.api.cr.TaskGroupSpecification;
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
public class TaskGroupFactoryMakerTest {

	@Configuration
	public Option[] configure() {
		return options(
			ConfigurationOptions.felixDS(),
			ConfigurationOptions.dotifyTasks(),
			junitBundles()
		);
	}

	@Inject @Filter(timeout=5000)
	TaskGroupFactoryMakerService factory;
	
	@Test
	public void testFactoryExists() {
		//Test
		assertNotNull("Factory exists.", factory);
	}
	
	@Test
	public void testSupportedSpecifications() {
		Set<TaskGroupSpecification> specs = factory.listSupportedSpecifications();
		assertEquals(10, specs.size());
		assertTrue(specs.contains(new TaskGroupSpecification("dtbook", "obfl", "sv-SE")));
		assertTrue(specs.contains(new TaskGroupSpecification("text", "obfl", "sv-SE")));
		assertTrue(specs.contains(new TaskGroupSpecification("xml", "obfl", "sv-SE")));
		assertTrue(specs.contains(new TaskGroupSpecification("txt", "obfl", "sv-SE")));
		assertTrue(specs.contains(new TaskGroupSpecification("obfl", "obfl", "sv-SE")));
		
		assertTrue(specs.contains(new TaskGroupSpecification("dtbook", "obfl", "en-US")));
		assertTrue(specs.contains(new TaskGroupSpecification("text", "obfl", "en-US")));
		assertTrue(specs.contains(new TaskGroupSpecification("xml", "obfl", "en-US")));
		assertTrue(specs.contains(new TaskGroupSpecification("txt", "obfl", "en-US")));
		assertTrue(specs.contains(new TaskGroupSpecification("obfl", "obfl", "en-US")));
	}
	
	@Test
	public void testGetFactoryForSwedish() {
		//Setup
		FilterLocale locale = FilterLocale.parse("sv-SE");
		
		//Test
		assertNotNull(factory.getFactory(new TaskGroupSpecification("xml", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("text", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("obfl", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("txt", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("dtbook", "obfl", locale.toString())));
	}
	
	@Test
	public void testGetFactoryForEnglish() {
		//Setup
		FilterLocale locale = FilterLocale.parse("en-US");
		
		//Test
		assertNotNull(factory.getFactory(new TaskGroupSpecification("xml", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("text", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("obfl", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("txt", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("dtbook", "obfl", locale.toString())));
	}
}
