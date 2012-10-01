package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.PaginatorProxy;

public class PaginatorProxyImpl implements PaginatorProxy {

	public Paginator newPaginator() {
		return new PaginatorImpl();
	}

}
