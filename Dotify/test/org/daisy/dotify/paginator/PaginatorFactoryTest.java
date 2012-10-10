package org.daisy.dotify.paginator;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorFactory;
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
