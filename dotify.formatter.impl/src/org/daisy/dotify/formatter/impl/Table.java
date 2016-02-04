package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TableProperties;
import org.daisy.dotify.common.text.StringTools;

class Table extends Block {
	private int headerRows;
	private final Stack<TableRow> rows;
	private final TableProperties tableProps;

	Table(TableProperties tableProps, RowDataProperties rdp) {
		super(null, rdp);
		this.tableProps = tableProps;
		headerRows = 0;
		rows = new Stack<>();
	}

	public void beginsTableBody() {
		headerRows = rows.size();
	}

	public void beginsTableRow() {
		if (!rows.empty()) {
			rows.peek().endsTableCell();
		}
		TableRow ret = new TableRow();
		rows.add(ret);
	}

	public FormatterCore beginsTableCell(TableCellProperties props) {
		return rows.peek().beginsTableCell(props);
	}

	/**
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	private TableCell getCurrentCell() {
		return rows.peek().getCurrentCell();
	}

	@Override
	public void addSegment(TextSegment s) {
		((FormatterCoreImpl)getCurrentCell()).getCurrentBlock().addSegment(s);
	}
	
	@Override
	public void addSegment(Segment s) {
		((FormatterCoreImpl)getCurrentCell()).getCurrentBlock().addSegment(s);
	}

	@Override
	protected AbstractBlockContentManager newBlockContentManager(BlockContext context) {
		// FIXME: add row-span support
		int columnCount = countColumns();
		MarginProperties leftMargin = rdp.getLeftMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		MarginProperties rightMargin = rdp.getRightMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		int columnWidth = (context.getFlowWidth() 
				- leftMargin.getContent().length() 
				- rightMargin.getContent().length() 
				- tableProps.getTableColSpacing()*(columnCount-1)) / columnCount;
		DefaultContext dc = DefaultContext.from(context.getContext()).metaVolume(metaVolume).metaPage(metaPage).build();
		List<RowImpl> result = new ArrayList<RowImpl>();
		for (TableRow row : rows) {
			List<CellData> cellData = new ArrayList<>(); 
			for (TableCell cell : row) {
				List<Block> blocks = cell.getBlocks(context.getFcontext(), dc, context.getRefs());
				List<RowImpl> rowData = new ArrayList<>();
				for (Block block : blocks) {
					AbstractBlockContentManager bcm = block.getBlockContentManager(
							new BlockContext(columnWidth*cell.getColSpan(), context.getRefs(), dc, context.getFcontext())
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
				cellData.add(new CellData(rowData, cell.getColSpan()));
			}
			// render into rows
			for (int i=0; ; i++) {
				boolean empty = true;
				StringBuilder tableRow = new StringBuilder();
				for (int j=0; j<cellData.size(); j++) {
					CellData cr = cellData.get(j);
					String data = "";
					if (i<cr.rows.size()) {
						empty = false;
						//FIXME: get additional properties, such as left margin etc.
						// Align
						data = PageImpl.padLeft(columnWidth*cr.colSpan, cr.rows.get(i), context.getFcontext().getSpaceCharacter());
					}
					tableRow.append(data);
					// Fill (only after intermediary columns) 
					if (j<cellData.size()-1) {
						int length = (columnWidth+tableProps.getTableColSpacing())*cr.colSpan - data.length();
						tableRow.append(StringTools.fill(context.getFcontext().getSpaceCharacter(), length));
					}
				}
				if (empty) {
					break;
				} else {
					result.add(new RowImpl(tableRow.toString(), leftMargin, rightMargin));
				}
			}
		}
		return new TableBlockContentManager(context.getFlowWidth(), result, rdp, context.getFcontext());
	}
	
	private static class CellData {
		private final List<RowImpl> rows;
		private final int colSpan;
		CellData(List<RowImpl> rows, int colSpan) {
			this.rows = rows;
			this.colSpan = colSpan;
		}
	}
	
	private int countColumns() {
		int cc = 0;
		// calculate the number of columns based on the first row
		// if subsequent rows differ, report it as an error
		for (TableCell c : rows.get(0)) {
			cc += Math.max(c.getColSpan(), 1);
		}
		return cc;
	}

}
