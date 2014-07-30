package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.HashMap;

/**
 * Provides an implementation of the paginator interface. This class should
 * not be used directly, use the corresponding factory methods instead.
 * 
 * @author Joel Håkansson
 */
public class PaginatorImpl {
	private final FormatterContext context;
	private final Iterable<BlockSequence> fs;
	private final HashMap<String, ContentCollectionImpl> collections;

	public PaginatorImpl(FormatterContext context, Iterable<BlockSequence> fs, HashMap<String, ContentCollectionImpl> collections) {
		this.context = context;
		this.fs = fs;
		this.collections = collections;
	}
	
	/**
	 * Paginates the supplied block sequence
	 * @param refs the cross references to use
	 * @throws IOException if IO fails
	 */
	public PageStructBuilder paginate(CrossReferences refs, DefaultContext rcontext) throws PaginatorException {
		restart:while (true) {
			PageStructBuilder pageStruct = new PageStructBuilder(context);
			for (BlockSequence seq : fs) {
				if (!pageStruct.newSequence(seq, refs, rcontext, collections)) {
					continue restart;
				}
			}
			return pageStruct;
		}
	}

}
