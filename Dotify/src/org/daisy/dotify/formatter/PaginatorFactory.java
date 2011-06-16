package org.daisy.dotify.formatter;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

public class PaginatorFactory {
	
	protected PaginatorFactory() {
		
	}

	public static PaginatorFactory newInstance() {
		Iterator<PaginatorFactory> i = ServiceRegistry.lookupProviders(PaginatorFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new PaginatorFactory();
	}
	
	public Paginator newPaginator() {
		Iterator<Paginator> i = ServiceRegistry.lookupProviders(Paginator.class);
		while (i.hasNext()) {
			return i.next();
		}
		throw new RuntimeException("Cannot find paginator.");
	}
}
