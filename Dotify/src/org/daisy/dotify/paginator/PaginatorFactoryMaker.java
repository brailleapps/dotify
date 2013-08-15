package org.daisy.dotify.paginator;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

/**
 * Provides a factory for paginators. The factory will instantiate 
 * the first Paginator it encounters when querying the services API.
 *  
 * @author Joel Håkansson
 */
public class PaginatorFactoryMaker {
	private final PaginatorFactory proxy;
	
	protected PaginatorFactoryMaker() {
		//Gets the first paginator (assumes there is at least one).
		this.proxy = ServiceRegistry.lookupProviders(PaginatorFactory.class).next();
	}

	public static PaginatorFactoryMaker newInstance() {
		Iterator<PaginatorFactoryMaker> i = ServiceRegistry.lookupProviders(PaginatorFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new PaginatorFactoryMaker();
	}
	
	public Paginator newPaginator() {
		return proxy.newPaginator();
	}
}
