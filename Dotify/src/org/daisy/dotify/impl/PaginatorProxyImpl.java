package org.daisy.dotify.impl;

import org.daisy.dotify.impl.paginator.PaginatorImpl;
import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorProxy;

/**
 * Provides a paginator proxy implementation. This class is intended to be instantiated
 * by the paginator factory, and is not part of the public API.
 * @author Joel Håkansson
 */
public class PaginatorProxyImpl implements PaginatorProxy {

	public Paginator newPaginator() {
		return new PaginatorImpl();
	}

}
