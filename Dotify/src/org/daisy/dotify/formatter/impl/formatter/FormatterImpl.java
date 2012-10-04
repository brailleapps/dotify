package org.daisy.dotify.formatter.impl.formatter;

import java.io.IOException;
import java.util.Stack;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.core.NumeralField.NumeralStyle;
import org.daisy.dotify.formatter.dom.BlockProperties;
import org.daisy.dotify.formatter.dom.BlockStruct;
import org.daisy.dotify.formatter.dom.FormattingTypes;
import org.daisy.dotify.formatter.dom.FormattingTypes.Keep;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Leader;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.RowDataProperties;
import org.daisy.dotify.formatter.dom.SequenceProperties;
import org.daisy.dotify.formatter.dom.TextProperties;
import org.daisy.dotify.formatter.utils.BlockHandler.ListItem;
import org.daisy.dotify.tools.StateObject;
import org.daisy.dotify.translator.BrailleTranslator;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson, TPB
 */
public class FormatterImpl implements Formatter {
	private int leftMargin;
	private int rightMargin;
	private final BlockStructImpl flowStruct;
	private final Stack<BlockProperties> context;
	//private boolean firstRow;
	private final StateObject state;
	//private CrossReferences refs;
	//private StringFilter filter;

	private BrailleTranslator translator;
	//private FilterLocale locale;
	//private BlockHandler bh;
	
	private int blockIndent;
	private Stack<Integer> blockIndentParent;
	private ListItem listItem;

	// TODO: fix recursive keep problem
	// TODO: Implement SpanProperites
	// TODO: Implement floating elements
	/**
	 * Creates a new formatter
	 */
	public FormatterImpl() {
		//this.filters = builder.filtersFactory.getDefault();
		this.context = new Stack<BlockProperties>();
		this.leftMargin = 0;
		this.rightMargin = 0;
		this.flowStruct = new BlockStructImpl(); //masters
		this.state = new StateObject();
		//this.filter = null;
		//this.refs = null;
		this.listItem = null;
	}


	public void setBrailleTranslator(BrailleTranslator translator) {
		state.assertUnopened();
		this.translator = translator;
	}
/*
	public void setLocale(FilterLocale locale) {
		state.assertUnopened();
		this.locale = locale;
		filter = null;
	}*/

	public void open() {
		state.assertUnopened();
		//bh = new BlockHandler(getDefaultFilter());
		this.blockIndent = 0;
		this.blockIndentParent = new Stack<Integer>();
		blockIndentParent.add(0);
		state.open();
	}

	public void addLayoutMaster(String name, LayoutMaster master) {
		flowStruct.addLayoutMaster(name, master);
	}

	public void addChars(CharSequence c, TextProperties p) {
		state.assertOpen();
		assert context.size()!=0;
		if (context.size()==0) return;
		BlockImpl bl = flowStruct.getCurrentSequence().getCurrentBlock();
		if (listItem!=null) {
			//append to this block
			bl.setListItem(listItem.getLabel(), listItem.getType());
			//list item has been used now, discard
			listItem = null;
		}
		bl.addChars(c, p, context.peek());		
	}
	// END Using BlockHandler

	public void insertMarker(Marker m) {
		//FIXME: this does not work
		state.assertOpen();
		flowStruct.getCurrentSequence().getCurrentBlock().addMarker(m);
	}
	
	public void startBlock(BlockProperties p) {
		startBlock(p, null);
	}

	public void startBlock(BlockProperties p, String blockId) {
		state.assertOpen();
		leftMargin += p.getLeftMargin();
		rightMargin += p.getRightMargin();
		if (context.size()>0) {
			addToBlockIndent(context.peek().getBlockIndent());
		}
		RowDataProperties rdp = new RowDataProperties.Builder(
				getTranslator(), flowStruct.getCurrentSequence().getLayoutMaster()).
					blockIndent(blockIndent).
					blockIndentParent(blockIndentParent.peek()).
					leftMargin(leftMargin).
					rightMargin(rightMargin).
					build();
		BlockImpl c = flowStruct.getCurrentSequence().newBlock(blockId, rdp);
		if (context.size()>0) {
			if (context.peek().getListType()!=FormattingTypes.ListStyle.NONE) {
				String listLabel;
				switch (context.peek().getListType()) {
				case OL:
					listLabel = context.peek().nextListNumber()+""; break;
				case UL:
					listLabel = "•";
					break;
				case PL: default:
					listLabel = "";
				}
				listItem = new ListItem(listLabel, context.peek().getListType());
			}
		}
		c.addSpaceBefore(p.getTopMargin());
		c.setBreakBeforeType(p.getBreakBeforeType());
		c.setKeepType(p.getKeepType());
		c.setKeepWithNext(p.getKeepWithNext());
		c.setIdentifier(p.getIdentifier());
		c.setKeepWithNextSheets(p.getKeepWithNextSheets());
		context.push(p);
		//firstRow = true;
	}
	
	public void endBlock() {
		state.assertOpen();
		if (listItem!=null) {
			addChars("", new TextProperties.Builder(null).build());
		}
		BlockProperties p = context.pop();
		flowStruct.getCurrentSequence().getCurrentBlock().addSpaceAfter(p.getBottomMargin());
		flowStruct.getCurrentSequence().getCurrentBlock().setKeepWithPreviousSheets(p.getKeepWithPreviousSheets());
		leftMargin -= p.getLeftMargin();
		rightMargin -= p.getRightMargin();
		if (context.size()>0) {
			Keep keep = context.peek().getKeepType();
			int next = context.peek().getKeepWithNext();
			subtractFromBlockIndent(context.peek().getBlockIndent());
			RowDataProperties rdp = new RowDataProperties.Builder(
					getTranslator(), flowStruct.getCurrentSequence().getLayoutMaster()).
						blockIndent(blockIndent).
						blockIndentParent(blockIndentParent.peek()).
						leftMargin(leftMargin).
						rightMargin(rightMargin).
						build();
			BlockImpl c = flowStruct.getCurrentSequence().newBlock(null, rdp);
			c.setKeepType(keep);
			c.setKeepWithNext(next);
		}
		//firstRow = true;
	}

	public void newSequence(SequenceProperties p) {
		state.assertOpen();
		flowStruct.newSequence(p);
	}

	public void insertLeader(Leader leader) {
		state.assertOpen();
		flowStruct.getCurrentSequence().getCurrentBlock().insertLeader(leader);
	}
	
	public void newLine() {
		state.assertOpen();
		flowStruct.getCurrentSequence().getCurrentBlock().newLine(leftMargin + context.peek().getTextIndent());
	}

	/**
	 * Gets the resulting data structure
	 * @return returns the data structure
	 * @throws IllegalStateException if not closed 
	 */
	public BlockStruct getFlowStruct() {
		state.assertClosed();
		return flowStruct;
	}
	
	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}

	public void endFloat() {
		state.assertOpen();
		// TODO implement float
		throw new UnsupportedOperationException("Not implemented");
	}

	public void insertAnchor(String ref) {
		state.assertOpen();
		// TODO implement anchor
		throw new UnsupportedOperationException("Not implemented");
	}

	public void startFloat(String id) {
		state.assertOpen();
		// TODO implement float
		throw new UnsupportedOperationException("Not implemented");
	}

/*
	public FilterFactory getFilterFactory() {
		return filtersFactory;
	}*/

/*
	public FilterLocale getFilterLocale() {
		return locale;
	}*/

/*
	public StringFilter getDefaultFilter() {
		if (filter == null) {
			filter = filtersFactory.newStringFilter(locale);
		}
		return filter;
	}*/
	
	private void addToBlockIndent(int value) {
		blockIndentParent.push(blockIndent);
		blockIndent += value;
	}
	
	private void subtractFromBlockIndent(int value) {
		int test = blockIndentParent.pop();
		blockIndent -= value;
		assert blockIndent==test;
	}

	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		flowStruct.getCurrentSequence().getCurrentBlock().insertReference(identifier, numeralStyle);
	}


	public BrailleTranslator getTranslator() {
		return translator;
	}

}
