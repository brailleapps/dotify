package org.daisy.dotify.formatter.dom;




/**
 * BlockStruct is a pull interface for the first step of the layout process
 * @author Joel Håkansson
 */
public interface BlockStruct {
	
	public Iterable<BlockSequence> getBlockSequenceIterable();

}