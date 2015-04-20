package org.daisy.dotify.formatter.impl;

import java.util.List;

public interface BlockGroup {

	public List<Block> getBlocks(FormatterContext context, DefaultContext c, CrossReferences crh);
	public boolean isGenerated();
}
