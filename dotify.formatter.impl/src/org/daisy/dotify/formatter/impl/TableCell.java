package org.daisy.dotify.formatter.impl;


import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.TableCellProperties;

class TableCell extends FormatterCoreImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -673589204065659433L;
	private final TableCellInfo info;
	private CellData rendered;


	TableCell(TableCellProperties props, GridPoint p) {
		this(props, false, p);
	}
	
	TableCell(TableCellProperties props, boolean discardIdentifiers, GridPoint p) {
		super(discardIdentifiers);
		this.info = new TableCellInfo(props, p);
		this.rendered = null;
	}

	TableCellInfo getInfo() {
		return info;
	}
	
	CellData render(FormatterContext context, DefaultContext c, CrossReferences crh, int flowWidth) {
		List<RowImpl> rowData = new ArrayList<>();
		List<Block> blocks = getBlocks(context, c, crh);
		for (Block block : blocks) {
			AbstractBlockContentManager bcm = block.getBlockContentManager(
					new BlockContext(flowWidth, crh, c, context)
					);
			//FIXME: get additional data from bcm
			rowData.addAll(bcm.getCollapsiblePreContentRows());
			rowData.addAll(bcm.getInnerPreContentRows());
			for (RowImpl r2 : bcm) {
				rowData.add(r2);
			}
			rowData.addAll(bcm.getPostContentRows());
			rowData.addAll(bcm.getSkippablePostContentRows());
		}
		rendered = new CellData(rowData, flowWidth, info);
		return rendered;
	}

	CellData getRendered() {
		return rendered;
	}

}
