package org.daisy.dotify.formatter;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class PaginatorFactoryTest {

	@Test
	public void testFactory() {
		//setup
		Paginator f = PaginatorFactory.newInstance().newPaginator();
		//test
		assertTrue("Assert that paginator can be instantiated", f!=null);
	}
}
