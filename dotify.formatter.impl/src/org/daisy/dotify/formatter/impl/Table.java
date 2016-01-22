package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
		if (tableProps.getTableRowSpacing()>0) {
			throw new UnsupportedOperationException("Table row spacing > 0 is not implemented.");
		}
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
		int columnCount = countColumns();
		MarginProperties leftMargin = rdp.getLeftMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		MarginProperties rightMargin = rdp.getRightMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		int columnWidth = (context.getFlowWidth() 
				- leftMargin.getContent().length() 
				- rightMargin.getContent().length() 
				- tableProps.getTableColSpacing()*(columnCount-1)) / columnCount;
		int[] cw = new int[columnCount];
		Arrays.fill(cw, columnWidth);
		DefaultContext dc = DefaultContext.from(context.getContext()).metaVolume(metaVolume).metaPage(metaPage).build();
		List<RowImpl> result = renderTable(cw, context, dc, leftMargin, rightMargin);
		return new TableBlockContentManager(context.getFlowWidth(), result, rdp, context.getFcontext());
	}
	
	private List<RowImpl> renderTable(int[] columnWidth, BlockContext context, DefaultContext dc, MarginProperties leftMargin, MarginProperties rightMargin) {
		List<RowImpl> result = new ArrayList<RowImpl>();
		for (TableRow row : rows) {
			List<CellData> cellData = new ArrayList<>();
			int ci = 0;
			for (TableCell cell : row) {
				// FIXME: add row-span support
				if (cell.getRowSpan()>1) {
					throw new UnsupportedOperationException("Table cell with row span > 1 is not implemented.");
				}
				List<Block> blocks = cell.getBlocks(context.getFcontext(), dc, context.getRefs());
				List<RowImpl> rowData = new ArrayList<>();
				int flowWidth = 0;
				for (int j = 0; j<cell.getColSpan(); j++) {
					flowWidth += columnWidth[ci];
					ci++;
				}
				for (Block block : blocks) {
					AbstractBlockContentManager bcm = block.getBlockContentManager(
							new BlockContext(flowWidth, context.getRefs(), dc, context.getFcontext())
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
				cellData.add(new CellData(rowData, cell.getColSpan(), flowWidth));
			}
			// render into rows
			boolean tableRowHasData = false;
			for (int i=0; ; i++) {
				boolean empty = true;
				StringBuilder tableRow = new StringBuilder();
				for (int j=0; j<cellData.size(); j++) {
					CellData cr = cellData.get(j);
					String data = "";
					if (i<cr.rows.size()) {
						empty = false;
						// Align
						data = PageImpl.padLeft(cr.cellWidth, cr.rows.get(i), context.getFcontext().getSpaceCharacter());
					}
					tableRow.append(data);
					// Fill (only after intermediary columns) 
					if (j<cellData.size()-1) {
						int length = cr.cellWidth+(tableProps.getTableColSpacing())*cr.colSpan - data.length();
						tableRow.append(StringTools.fill(context.getFcontext().getSpaceCharacter(), length));
					}
				}
				if (empty) {
					break;
				} else {
					tableRowHasData = true;
					RowImpl r = new RowImpl(tableRow.toString(), leftMargin, rightMargin);
					r.setRowSpacing(tableProps.getRowSpacing());
					//FIXME: this will keep the whole table row together (if possible), but it could be more advanced
					r.setAllowsBreakAfter(false);
					result.add(r);
				}
			}
			if (tableRowHasData) {
				result.get(result.size()-1).setAllowsBreakAfter(true);
			}
		}
		return result;
	}
	
	private static class CellData {
		private final List<RowImpl> rows;
		private final int colSpan;
		private final int cellWidth;
		CellData(List<RowImpl> rows, int colSpan, int cellWidth) {
			this.rows = rows;
			this.colSpan = colSpan;
			this.cellWidth = cellWidth;
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
