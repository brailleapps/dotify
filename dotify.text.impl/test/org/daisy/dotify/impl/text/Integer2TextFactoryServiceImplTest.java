package org.daisy.dotify.impl.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.junit.Test;

public class Integer2TextFactoryServiceImplTest {

	@Test
	public void testFactory() throws Integer2TextConfigurationException {
		Integer2TextFactoryServiceImpl factory = new Integer2TextFactoryServiceImpl();
		assertEquals(8, factory.listLocales().size());
		assertNotNull(factory.newFactory().newInteger2Text("no-no"));
	}
}
