package spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.daisy.dotify.api.cr.TaskGroupSpecification;
import org.daisy.dotify.common.text.FilterLocale;
import org.daisy.dotify.consumer.cr.InputManagerFactoryMaker;
import org.junit.Test;

public class InputManagerFactoryMakerTest {

	@Test
	public void testFactoryExists() {
		//Setup
		InputManagerFactoryMaker factory = InputManagerFactoryMaker.newInstance();
		
		//Test
		assertNotNull("Factory exists.", factory);
	}
	
	@Test
	public void testSupportedSpecifications() {
		//Setup
		InputManagerFactoryMaker factory = InputManagerFactoryMaker.newInstance();
		Set<TaskGroupSpecification> specs = factory.listSupportedSpecifications();

		//Test
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
		InputManagerFactoryMaker factory = InputManagerFactoryMaker.newInstance();
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
		InputManagerFactoryMaker factory = InputManagerFactoryMaker.newInstance();
		FilterLocale locale = FilterLocale.parse("en-US");
		
		//Test
		assertNotNull(factory.getFactory(new TaskGroupSpecification("xml", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("text", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("obfl", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("txt", "obfl", locale.toString())));
		assertNotNull(factory.getFactory(new TaskGroupSpecification("dtbook", "obfl", locale.toString())));
	}
}