package org.daisy.dotify.impl;

import org.daisy.dotify.impl.paginator.PaginatorImpl;
import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorProxy;

public class PaginatorProxyImpl implements PaginatorProxy {

	public Paginator newPaginator() {
		return new PaginatorImpl();
	}

}
