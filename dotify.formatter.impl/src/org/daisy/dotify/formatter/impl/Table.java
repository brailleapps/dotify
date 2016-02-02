package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TableProperties;
import org.daisy.dotify.api.translator.Border;
import org.daisy.dotify.api.translator.BorderSpecification;
import org.daisy.dotify.api.translator.BorderSpecification.Style;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.common.text.StringTools;

class Table extends Block {
	private final static Logger logger = Logger.getLogger(Table.class.getCanonicalName());
	private int headerRows;
	private final TableData td;
	private final TableProperties tableProps;
	private Map<String, Result> resultCache;
	private final TableBorderHandler tbh;

	Table(TableProperties tableProps, RowDataProperties rdp, TextBorderFactoryMakerService tbf, String mode) {
		super(null, rdp);
		this.tableProps = tableProps;
		if (tableProps.getTableRowSpacing()>0) {
			throw new UnsupportedOperationException("Table row spacing > 0 is not implemented.");
		}
		this.headerRows = 0;
		this.td = new TableData();
		this.tbh = new TableBorderHandler(tableProps.getTableColSpacing(), tbf, mode);
	}

	public void beginsTableBody() {
		headerRows = td.getRowCount();
	}

	public void beginsTableRow() {
		td.beginsTableRow();
	}

	public FormatterCore beginsTableCell(TableCellProperties props) {
		return td.beginsTableCell(props);
	}

	@Override
	public void addSegment(TextSegment s) {
		((FormatterCoreImpl)td.getCurrentCell()).getCurrentBlock().addSegment(s);
	}
	
	@Override
	public void addSegment(Segment s) {
		((FormatterCoreImpl)td.getCurrentCell()).getCurrentBlock().addSegment(s);
	}

	@Override
	protected AbstractBlockContentManager newBlockContentManager(BlockContext context) {
		int columnCount = countColumns();
		int rowCount = countRows();
		int[] colSpace = calcSpacings(new ColumnSpaceCalculator(rowCount, columnCount));
		//int[] rowSpace = calcSpacings(new RowSpaceCalculator(rowCount, columnCount));
		MarginProperties leftMargin = rdp.getLeftMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		MarginProperties rightMargin = rdp.getRightMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		int columnWidth = (context.getFlowWidth() 
				- leftMargin.getContent().length() 
				- rightMargin.getContent().length()
				); //- tableProps.getTableColSpacing()*(columnCount-1))/ columnCount;
		for (int i : colSpace) {
			columnWidth -= i; 
		}
		columnWidth = columnWidth / columnCount;
		int[] currentColumnWidth = new int[columnCount];
		Arrays.fill(currentColumnWidth, columnWidth);
		DefaultContext dc = DefaultContext.from(context.getContext()).metaVolume(metaVolume).metaPage(metaPage).build();
		resultCache = new HashMap<>();
		Result r = minimizeCost(currentColumnWidth, colSpace, -1, tableProps.getPreferredEmtpySpace(), context, dc, leftMargin, rightMargin);
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
	
	private Result minimizeCost(int[] columnWidth, int[] colSpacing, int direction, int spacePreferred, BlockContext context, DefaultContext dc, MarginProperties leftMargin, MarginProperties rightMargin) {
		int columnCount = columnWidth.length;
		int[] currentColumnWidth = Arrays.copyOf(columnWidth, columnWidth.length);
		Result[] results = new Result[columnCount];
		Result currentResult;
		//base result
		currentResult = renderTableWithCache(spacePreferred, currentColumnWidth, colSpacing, context, dc, leftMargin, rightMargin);
		while (true) {
			// render all possibilities
			for (int i=0; i<columnCount; i++) {
				if (currentColumnWidth[i]>=1) {
					// change value
					currentColumnWidth[i] = currentColumnWidth[i] + direction;
					results[i] = renderTableWithCache(spacePreferred, currentColumnWidth, colSpacing, context, dc, leftMargin, rightMargin);
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
	
	private Result renderTableWithCache(int spacePreferred, int[] columnWidth, int[] colSpacing, BlockContext context, DefaultContext dc, MarginProperties leftMargin, MarginProperties rightMargin) {
		String key = toKey(columnWidth);
		Result r = resultCache.get(key);
		if (r==null) {
			logger.finest("Calculating new result for key: " + key);
			r = new Result();
			r.cost = new TableCostImpl(spacePreferred);
			r.rows = renderTable(columnWidth, colSpacing, r.cost, context, dc, leftMargin, rightMargin);
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
	
	private List<RowImpl> renderTable(int[] columnWidth, int[] colSpacing, TableCost costFunc, BlockContext context, DefaultContext dc, MarginProperties leftMargin, MarginProperties rightMargin) {
		List<RowImpl> result = new ArrayList<RowImpl>();
		for (TableRow row : td) {
			List<CellData> cellData = new ArrayList<>();
			for (TableCell cell : row) {
				// FIXME: add row-span support
				if (cell.getInfo().getRowSpan()>1) {
					throw new UnsupportedOperationException("Table cell with row span > 1 is not implemented.");
				}
				int flowWidth = 0;
				int ci = cell.getInfo().getStartingPoint().getCol();
				for (int j=0; j<cell.getInfo().getColSpan(); j++) {
					if (j>0) {
						flowWidth += colSpacing[ci-1+j];
					}
					flowWidth += columnWidth[ci+j];
				}
				CellData cd = cell.render(context.getFcontext(), dc, context.getRefs(), flowWidth);
				cellData.add(cd);
				costFunc.addCell(cd.getRows(), flowWidth);
			}
			// render into rows
			boolean tableRowHasData = false;
			for (;;) { //while true
				boolean empty = true;
				StringBuilder tableRow = new StringBuilder();
				List<Marker> markers = new ArrayList<>();
				List<String> anchors = new ArrayList<>();
				for (int j=0; j<cellData.size(); j++) {
					CellData cr = cellData.get(j);
					String data = "";
					if (cr.getRowIterator().hasNext()) { //FIXME: row span here
						empty = false;
						RowImpl r = cr.getRowIterator().next();
						// Align
						data = PageImpl.padLeft(cr.getCellWidth(), r, context.getFcontext().getSpaceCharacter());
						markers.addAll(r.getMarkers());
						anchors.addAll(r.getAnchors());
					}
					tableRow.append(data);
					// Fill (only after intermediary columns)
					//FIXME: this doesn't work if the last column on the preceding row is a cell with rowspan > 1
					if (j<cellData.size()-1) {
						TableCell c = td.cellForGrid(cr.getInfo().getStartingPoint().getRow(), cr.getInfo().getEndPoint().getCol()+1);
						String border;
						if (c==null) {
							border = "";
						} else {
							border = tbh.getSharedColumnString(cr.getInfo().getBorder(), c.getInfo().getBorder(), context);
						}
						int length = cr.getCellWidth()+colSpacing[cr.getInfo().getEndPoint().getCol()] - data.length() - border.length();
						if (length>0) {
							tableRow.append(StringTools.fill(context.getFcontext().getSpaceCharacter(), length));
						}
						tableRow.append(border);
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

	private int[] calcSpacings(GridSpaceCalculator comp) {
		Iterators it = comp.getIterator();
		int[] ret = new int[it.inner-1];
		Arrays.fill(ret, 0);
		TableCell cell1;
		for (TableRow r : td) {
			int index = -1;
			cell1 = null;
			for (TableCell c : r) {
				if (cell1!=null) {
					index += Math.max(comp.getSpan(cell1), 1);
					ret[index] = Math.max(ret[index], comp.getSpacingValue(cell1, c));
				}
				cell1 = c;
			}
		}
		return ret;
	}
	
	private static class Iterators {
		private final int inner, outer;
		Iterators(int outer, int inner) {
			this.outer = outer;
			this.inner = inner;
		}
	}
	
	private static interface GridSpaceCalculator {
		Iterators getIterator();
		TableCell getFirstCell(int outer, int inner);
		TableCell getSecondCell(int outer, int inner);
		int getSpan(TableCell cell);
		int getSpacingValue(TableCell cell1, TableCell cell2);
	}
	
	private class ColumnSpaceCalculator implements GridSpaceCalculator {
		private final int rowCount, colCount;
		
		public ColumnSpaceCalculator(int rowCount, int colCount) {
			this.rowCount = rowCount;
			this.colCount = colCount;
		}
		
		@Override
		public Iterators getIterator() {
			return new Iterators(rowCount, colCount);
		}

		@Override
		public TableCell getFirstCell(int outer, int inner) {
			return td.getRow(outer).getCell(inner);
		}
		
		public BorderSpecification getBorderBefore(Border border) {
			return border.getLeft();
		}

		public BorderSpecification getBorderAfter(Border border) {
			return border.getRight();
		}

		@Override
		public TableCell getSecondCell(int outer, int inner) {
			return td.getRow(outer).getCell(inner+1);
		}

		@Override
		public int getSpan(TableCell cell) {
			return cell.getInfo().getColSpan();
		}

		public int getTableSpacing() {
			return tableProps.getTableColSpacing();
		}
		
		@Override
		public int getSpacingValue(TableCell cell1, TableCell cell2) {
			int b1 = cell1.getInfo().getBorder()!=null && getBorderAfter(cell1.getInfo().getBorder()).getStyle()!=Style.NONE?1:0;
			int b2 = cell2.getInfo().getBorder()!=null && getBorderBefore(cell2.getInfo().getBorder()).getStyle()!=Style.NONE?1:0;
			if (getTableSpacing()>0) {
				return b1 + b2 + getTableSpacing();					
			} else {
				return Math.max(b1, b2);
			}
		}
	}
	

	
	private class RowSpaceCalculator implements GridSpaceCalculator {
		private final int rowCount, colCount;
		public RowSpaceCalculator(int rowCount, int colCount) {
			this.rowCount = rowCount;
			this.colCount = colCount;
		}
		@Override
		public Iterators getIterator() {
			return new Iterators(colCount, rowCount);
		}

		@Override
		public TableCell getFirstCell(int outer, int inner) {
			return td.getRow(inner).getCell(outer);
		}

		public BorderSpecification getBorderBefore(Border border) {
			return border.getTop();
		}

		public BorderSpecification getBorderAfter(Border border) {
			return border.getBottom();
		}

		@Override
		public TableCell getSecondCell(int outer, int inner) {
			return td.getRow(inner+1).getCell(outer);
		}

		@Override
		public int getSpan(TableCell cell) {
			return cell.getInfo().getRowSpan();
		}

		public int getTableSpacing() {
			return tableProps.getTableRowSpacing();
		}
		
		@Override
		public int getSpacingValue(TableCell cell1, TableCell cell2) {
			int b1 = cell1.getInfo().getBorder()!=null && getBorderAfter(cell1.getInfo().getBorder()).getStyle()!=Style.NONE?1:0;
			int b2 = cell2.getInfo().getBorder()!=null && getBorderBefore(cell2.getInfo().getBorder()).getStyle()!=Style.NONE?1:0;
			if (getTableSpacing()>0) {
				return b1 + b2 + getTableSpacing();					
			} else {
				return Math.max(b1, b2);
			}
		}
		
	}


	
	private int countColumns() {
		int cc = 0;
		// calculate the number of columns based on the first row
		// if subsequent rows differ, report it as an error
		for (TableCell c : td.getRow(0)) {
			cc += Math.max(c.getInfo().getColSpan(), 1);
		}
		return cc;
	}
	
	private int countRows() {
		int cc = 0;
		// calculate the number of rows based on the first colum
		// if subsequent columns differ, report it as an error
		for (TableRow r : td) {
			cc += Math.max(r.getCell(0).getInfo().getRowSpan(), 1);
		}
		return cc;
	}

}
