package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TableProperties;
import org.daisy.dotify.common.text.StringTools;

class Table extends Block {
	private final static Logger logger = Logger.getLogger(Table.class.getCanonicalName());
	private int headerRows;
	private final Stack<TableRow> rows;
	private final TableProperties tableProps;
	private Map<String, Result> resultCache;

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
		int[] currentColumnWidth = new int[columnCount];
		Arrays.fill(currentColumnWidth, columnWidth);
		DefaultContext dc = DefaultContext.from(context.getContext()).metaVolume(metaVolume).metaPage(metaPage).build();
		resultCache = new HashMap<>();
		Result r = minimizeCost(currentColumnWidth, -1, tableProps.getPreferredEmtpySpace(), context, dc, leftMargin, rightMargin);
		/*
		TableCost costFunc = new TableCostImpl(spacePreferred);
		List<RowImpl> result = renderTable(currentColumnWidth, costFunc, context, dc, leftMargin, rightMargin);
		boolean costReduced = true;
		int[] minColumnWidth = new int[columnCount];
		Arrays.fill(minColumnWidth, columnWidth);
		while (costReduced) {
			costReduced = false;
			for (int i=0; i<columnCount; i++) {
				if (minColumnWidth[i]>currentColumnWidth[i]-1) {
					currentColumnWidth[i]--;
					TableCost costFunc2 = new TableCostImpl(spacePreferred);
					List<RowImpl> result2 = renderTable(currentColumnWidth, costFunc2, context, dc, leftMargin, rightMargin);
					minColumnWidth[i]=currentColumnWidth[i];
					if (costFunc2.getCost()<costFunc.getCost()) {
						result = result2;
						costFunc = costFunc2;
						costReduced = true;
					} else {
						//restore column
						currentColumnWidth[i]++;
					}
					System.out.println("COST: " + costFunc.getCost());
				} else {
					System.out.println("NO CALC");
				}
			}
		}*/
		
		return new TableBlockContentManager(context.getFlowWidth(), r.rows, rdp, context.getFcontext());
	}
	
	private Result minimizeCost(int[] columnWidth, int direction, int spacePreferred, BlockContext context, DefaultContext dc, MarginProperties leftMargin, MarginProperties rightMargin) {
		int columnCount = columnWidth.length;
		int[] currentColumnWidth = Arrays.copyOf(columnWidth, columnWidth.length);
		Result[] results = new Result[columnCount];
		Result currentResult;
		//base result
		currentResult = renderTableWithCache(spacePreferred, currentColumnWidth, context, dc, leftMargin, rightMargin);
		while (true) {
			// render all possibilities
			for (int i=0; i<columnCount; i++) {
				if (currentColumnWidth[i]>=1) {
					// change value
					currentColumnWidth[i] = currentColumnWidth[i] + direction;
					results[i] = renderTableWithCache(spacePreferred, currentColumnWidth, context, dc, leftMargin, rightMargin);
					// restore value
					currentColumnWidth[i] = currentColumnWidth[i] - direction;
				}
			}
			// select
			Result min = min(currentResult, results);
			if (min!=currentResult) {
				currentResult = min;
				currentColumnWidth = min.widths;
			} else {
				break;
			}
		}
		return currentResult;
	}
	
	private static class Result {
		List<RowImpl> rows;
		TableCost cost;
		int[] widths;
	}
	
	private static Result min(Result v, Result ... values) {
		if (values.length<1) {
			throw new IllegalArgumentException("No values");
		}
		Result ret = v;
		for (int i=0; i<values.length; i++) {
			//System.out.println("COST: " + values[i].cost.getCost());
			//if new value is less than existing value, replace it
			ret = values[i].cost.getCost()<ret.cost.getCost()?values[i]:ret;
		}
		return ret;
	}
	
	private Result renderTableWithCache(int spacePreferred, int[] columnWidth, BlockContext context, DefaultContext dc, MarginProperties leftMargin, MarginProperties rightMargin) {
		String key = toKey(columnWidth);
		Result r = resultCache.get(key);
		if (r==null) {
			logger.finest("Calculating new result for key: " + key);
			r = new Result();
			r.cost = new TableCostImpl(spacePreferred);
			r.rows = renderTable(columnWidth, r.cost, context, dc, leftMargin, rightMargin);
			r.widths = Arrays.copyOf(columnWidth, columnWidth.length);
			logger.finest("Cost for solution: " + r.cost.getCost());
			resultCache.put(toKey(columnWidth), r);
		} else {
			logger.finest("Using cached result with key: " + key);
		}
		return r;
	}
	
	private String toKey(int...values) {
		StringBuilder ret = new StringBuilder();
		for (int v : values) {
			ret.append(v).append(",");
		}
		return ret.toString();
	}
	
	private List<RowImpl> renderTable(int[] columnWidth, TableCost costFunc, BlockContext context, DefaultContext dc, MarginProperties leftMargin, MarginProperties rightMargin) {
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
				costFunc.addCell(rowData, flowWidth);
				
			}
			// render into rows
			boolean tableRowHasData = false;
			for (int i=0; ; i++) {
				boolean empty = true;
				StringBuilder tableRow = new StringBuilder();
				List<Marker> markers = new ArrayList<>();
				List<String> anchors = new ArrayList<>();
				for (int j=0; j<cellData.size(); j++) {
					CellData cr = cellData.get(j);
					String data = "";
					if (i<cr.rows.size()) {
						empty = false;
						RowImpl r = cr.rows.get(i);
						// Align
						data = PageImpl.padLeft(cr.cellWidth, r, context.getFcontext().getSpaceCharacter());
						markers.addAll(r.getMarkers());
						anchors.addAll(r.getAnchors());
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
					r.addMarkers(markers);
					r.addAnchors(anchors);
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
		costFunc.completeTable(result);
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
