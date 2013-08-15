package org.daisy.dotify.paginator.impl;

import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorFactory;

/**
 * Provides a paginator proxy implementation. This class is intended to be instantiated
 * by the paginator factory, and is not part of the public API.
 * @author Joel Håkansson
 */
public class PaginatorFactoryImpl implements PaginatorFactory {

	public Paginator newPaginator() {
		return new PaginatorImpl();
	}

}
