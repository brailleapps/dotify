package org.daisy.dotify.formatter.impl;

import org.junit.Test;
import static org.junit.Assert.*;

public class LookupHandlerTest {
	private final static String key1 = "key1";
	private final static String value1 = "value1";

	@Test
	public void test_commit_vs_put_using_commit() {
		LookupHandler<String, String> lh = new LookupHandler<>();
		lh.put(key1, value1);
		assertEquals(value1, lh.get(key1)); // note that the act of getting the value is important to the state of the lookup handler
		lh.keep(key1, "other");
		lh.keep(key1, value1);
		lh.commit();
		//not dirty because value was changed back to the original value before commit
		assertFalse(lh.isDirty());
	}
	
	@Test
	public void test_commit_vs_put_using_put() {
		LookupHandler<String, String> lh = new LookupHandler<>();
		lh.put(key1, value1);
		assertEquals(value1, lh.get(key1)); // note that the act of getting the value is important to the state of the lookup handler
		lh.put(key1, "other");
		lh.put(key1, value1);
		//dirty because value has changed
		assertTrue(lh.isDirty());
	}
	
	@Test(expected=IllegalStateException.class)
	public void test_commit_exception() {
		LookupHandler<String, String> lh = new LookupHandler<>();
		lh.keep(key1, "other");
		lh.keep(key1, value1);
		lh.setDirty(false);
	}

}
