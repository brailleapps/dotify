package org.daisy.dotify.formatter;

import java.io.Closeable;
import java.io.IOException;

import org.daisy.dotify.formatter.dom.BlockSequence;
import org.daisy.dotify.formatter.dom.CrossReferences;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.PageStruct;
import org.daisy.dotify.formatter.dom.Row;



/**
 * <p>Breaks a stream of {@link Row} into pages.</p>
 * 
 * <p>The Paginator implementation is responsible for breaking
 * pages when required by the properties of the {@link LayoutMaster}. It
 * is also responsible for placing page dependent items such
 * as footnotes, but not headers and footers, as these might need
 * to search the entire page structure.</p>
 * 
 * <p>The final result is passed on to the {@link PagedMediaWriter}.</p>
 * 
 * @author Joel Håkansson, TPB
 *
 */
public interface Paginator extends Closeable {
	
	/**
	 * Opens for writing to the supplied writer 
	 */
	public void open(FormatterFactory formatterFactory);

	/**
	 * Paginates the block sequence
	 * @param fs The BlockSequences to paginate
	 * @param refs cross references
	 * @throws IOException
	 */
	public void paginate(Iterable<BlockSequence> fs, CrossReferences refs) throws IOException;
	
	public PageStruct getPageStruct();
}
