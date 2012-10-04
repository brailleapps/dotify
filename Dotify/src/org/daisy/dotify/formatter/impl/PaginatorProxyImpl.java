package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.PaginatorProxy;
import org.daisy.dotify.formatter.impl.paginator.PaginatorImpl;

public class PaginatorProxyImpl implements PaginatorProxy {

	public Paginator newPaginator() {
		return new PaginatorImpl();
	}

}
