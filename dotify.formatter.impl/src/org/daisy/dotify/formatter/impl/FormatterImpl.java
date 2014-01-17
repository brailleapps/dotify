package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.FormattingTypes.Keep;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.formatter.Volume;
import org.daisy.dotify.api.formatter.VolumeContentFormatter;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.tools.StateObject;
import org.daisy.dotify.tools.StringTools;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson
 */
public class FormatterImpl implements Formatter {
	private Stack<String> leftMargin;
	private Stack<String> rightMargin;
	private final BlockStructImpl flowStruct;
	private final Stack<BlockProperties> context;
	//private boolean firstRow;
	private final StateObject state;
	//private CrossReferences refs;
	//private StringFilter filter;

	private final BrailleTranslator translator;
	//private FilterLocale locale;
	//private BlockHandler bh;
	
	private int blockIndent;
	private Stack<Integer> blockIndentParent;
	private ListItem listItem;
	private final String marginChar;

	// TODO: fix recursive keep problem
	// TODO: Implement SpanProperites
	// TODO: Implement floating elements
	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslator translator) {
		//this.filters = builder.filtersFactory.getDefault();
		this.context = new Stack<BlockProperties>();
		this.leftMargin = new Stack<String>();
		this.rightMargin = new Stack<String>();
		this.flowStruct = new BlockStructImpl(); //masters
		this.state = new StateObject();
		//this.filter = null;
		//this.refs = null;
		this.listItem = null;
		this.translator = translator;
		this.marginChar = translator.translate(" ").getTranslatedRemainder();
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
	
	private MarginProperties stackString(List<String> inp, boolean reverse) {
		StringBuilder sb = new StringBuilder();
		if (reverse) {
			for (int i = inp.size()-1; i>=0; i--) {
				sb.append(inp.get(i));
			}
		} else {
			for (String s : inp) {
				sb.append(s);
			}
		}
		boolean isSpace = sb.toString().replaceAll(marginChar, "").length()==0;
		return new MarginProperties(sb.toString(), isSpace);
	}

	public void startBlock(BlockProperties p, String blockId) {
		state.assertOpen();
		String lb = "";
		String rb = "";
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			lb = t.getLeftBorder();
			rb = t.getRightBorder();
		}
		leftMargin.push(StringTools.fill(marginChar, p.getLeftMargin()));
		leftMargin.push(lb);
		rightMargin.push(StringTools.fill(marginChar, p.getRightMargin()));
		rightMargin.push(rb);
		if (context.size()>0) {
			addToBlockIndent(context.peek().getBlockIndent());
		}
		RowDataProperties.Builder rdp = new RowDataProperties.Builder(
				getTranslator(), flowStruct.getCurrentSequence().getLayoutMaster()).
					blockIndent(blockIndent).
					blockIndentParent(blockIndentParent.peek()).
					leftMargin(stackString(leftMargin, false)).
					leftMarginParent(stackString(leftMargin.subList(0, leftMargin.size()-1), false)).
					rightMargin(stackString(rightMargin, true)).
					rightMarginParent(stackString(rightMargin.subList(0, rightMargin.size()-1), true));
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
		c.setVerticalPosition(p.getVerticalPosition());
		context.push(p);
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			BlockImpl bi = flowStruct.getCurrentSequence().getCurrentBlock();
			if (t.getTopLeftCorner().length()+t.getTopBorder().length()+t.getTopRightCorner().length()>0) {
				bi.setLeadingDecoration(new SingleLineDecoration(t.getTopLeftCorner(), t.getTopBorder(), t.getTopRightCorner()));
			}
		}
		//firstRow = true;
	}
	
	public void endBlock() {
		state.assertOpen();
		if (listItem!=null) {
			addChars("", new TextProperties.Builder(null).build());
		}
		BlockProperties p = context.pop();
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			if (t.getBottomLeftCorner().length()+ t.getBottomBorder().length()+ t.getBottomRightCorner().length()>0) {
				flowStruct.getCurrentSequence().getCurrentBlock()
				.setTrailingDecoration(new SingleLineDecoration(t.getBottomLeftCorner(), t.getBottomBorder(), t.getBottomRightCorner()));
			}
		}
		flowStruct.getCurrentSequence().getCurrentBlock().addSpaceAfter(p.getBottomMargin());
		flowStruct.getCurrentSequence().getCurrentBlock().setKeepWithPreviousSheets(p.getKeepWithPreviousSheets());
		leftMargin.pop();
		leftMargin.pop();
		rightMargin.pop();
		rightMargin.pop();
		if (context.size()>0) {
			Keep keep = context.peek().getKeepType();
			int next = context.peek().getKeepWithNext();
			subtractFromBlockIndent(context.peek().getBlockIndent());
			RowDataProperties.Builder rdp = new RowDataProperties.Builder(
					getTranslator(), flowStruct.getCurrentSequence().getLayoutMaster()).
						blockIndent(blockIndent).
						blockIndentParent(blockIndentParent.peek()).
						leftMargin(stackString(leftMargin, false)).
						leftMarginParent(stackString(leftMargin.subList(0, leftMargin.size()-1), false)).
						rightMargin(stackString(rightMargin, true)).
						rightMarginParent(stackString(rightMargin.subList(0, rightMargin.size()-1), true));
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
		MarginProperties p = stackString(leftMargin, false);
		MarginProperties ret = new MarginProperties(p.getContent()+StringTools.fill(marginChar, context.peek().getTextIndent()), p.isSpaceOnly());
		flowStruct.getCurrentSequence().getCurrentBlock().newLine(ret);
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

	public Iterable<Volume> getVolumes(VolumeContentFormatter vcf) {
		PaginatorImpl paginator = new PaginatorImpl();
		paginator.open(getTranslator(), getFlowStruct().getBlockSequenceIterable());

		BookStruct bookStruct = new BookStruct(paginator, vcf, getTranslator());
		return bookStruct.getVolumes();
	}

}
