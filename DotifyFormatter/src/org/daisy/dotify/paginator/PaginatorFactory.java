package org.daisy.dotify.paginator;

/**
 * Provides a proxy for creating a pagintor implementation. Objects of this class are
 * detected by the paginator factory and their sole purpose is to create instances
 * of a paginator implementation.
 * 
 * @author Joel Håkansson
 */
public interface PaginatorFactory {

	/**
	 * Creates a new paginator.
	 * @return returns the new paginator
	 */
	public Paginator newPaginator();

}
