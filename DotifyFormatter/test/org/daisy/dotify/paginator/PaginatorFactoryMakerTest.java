package org.daisy.dotify.paginator;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorFactoryMaker;
import org.junit.Test;


public class PaginatorFactoryMakerTest {

	@Test
	public void testFactory() {
		//setup
		Paginator f = PaginatorFactoryMaker.newInstance().newPaginator();
		//test
		assertTrue("Assert that paginator can be instantiated", f!=null);
	}
}
