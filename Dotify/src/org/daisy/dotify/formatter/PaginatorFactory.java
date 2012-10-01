package org.daisy.dotify.formatter;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

/**
 * Provides a factory for paginators. The factory will instantiate 
 * the first Paginator it encounters when querying the services API.
 *  
 * @author Joel Håkansson
 */
public class PaginatorFactory {
	private final PaginatorProxy proxy;
	
	protected PaginatorFactory() {
		//Gets the first paginator (assumes there is at least one).
		this.proxy = ServiceRegistry.lookupProviders(PaginatorProxy.class).next();
	}

	public static PaginatorFactory newInstance() {
		Iterator<PaginatorFactory> i = ServiceRegistry.lookupProviders(PaginatorFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new PaginatorFactory();
	}
	
	public Paginator newPaginator() {
		return proxy.newPaginator();
	}
}
