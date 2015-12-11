package org.daisy.dotify.formatter.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Position;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;
import org.junit.Test;

public class BlockContentManagerTest {
	
	@Test
	public void testHangingIndent() throws TranslatorConfigurationException {
		//setup
		FormatterContext c = new FormatterContext(BrailleTranslatorFactoryMaker.newInstance(), "sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		Stack<Segment> segments = new Stack<>();
		for (int i=0; i<6; i++) {
			segments.push(new TextSegment("... ", new TextProperties.Builder("sv-SE").build()));
		}
		RowDataProperties rdp = new RowDataProperties.Builder().firstLineIndent(1).textIndent(3).build();
		CrossReferences refs = mock(CrossReferences.class);
		Context context = new DefaultContext(1, 1);
		BlockContentManager m = new BlockContentManager(10, segments, rdp, refs, context, c);

		//test
		assertEquals(3, m.getRowCount());
		Iterator<RowImpl> i = m.iterator();
		assertEquals("⠀⠄⠄⠄⠀⠄⠄⠄", i.next().getChars());
		assertEquals("⠀⠀⠀⠄⠄⠄⠀⠄⠄⠄", i.next().getChars());
		assertEquals("⠀⠀⠀⠄⠄⠄⠀⠄⠄⠄", i.next().getChars());
	}
	
	@Test
	public void testLeader() throws TranslatorConfigurationException {
		//setup
		FormatterContext c = new FormatterContext(BrailleTranslatorFactoryMaker.newInstance(), "sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		Stack<Segment> segments = new Stack<>();
		segments.push(new LeaderSegment(
				new Leader.Builder().align(org.daisy.dotify.api.formatter.Leader.Alignment.RIGHT).pattern(" ").position(new Position(1.0, true)).build())
		);
		segments.push(new TextSegment("...", new TextProperties.Builder("sv-SE").build()));

		RowDataProperties rdp = new RowDataProperties.Builder().firstLineIndent(1).textIndent(3).build();
		CrossReferences refs = mock(CrossReferences.class);
		Context context = new DefaultContext(1, 1);
		BlockContentManager m = new BlockContentManager(10, segments, rdp, refs, context, c);

		//test
		assertEquals(1, m.getRowCount());
		Iterator<RowImpl> i = m.iterator();
		assertEquals("⠀⠀⠀⠀⠀⠀⠀⠄⠄⠄", i.next().getChars());
	}
	
	@Test
	public void testNewLine() throws TranslatorConfigurationException {
		//setup
		FormatterContext c = new FormatterContext(BrailleTranslatorFactoryMaker.newInstance(), "sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		Stack<Segment> segments = new Stack<>();
		segments.push(new TextSegment("... ... ...", new TextProperties.Builder("sv-SE").build()));
		segments.push(new NewLineSegment());
		segments.push(new TextSegment("...", new TextProperties.Builder("sv-SE").build()));
		segments.push(new NewLineSegment());
		segments.push(new TextSegment("...", new TextProperties.Builder("sv-SE").build()));

		RowDataProperties rdp = new RowDataProperties.Builder().firstLineIndent(1).textIndent(3).build();
		CrossReferences refs = mock(CrossReferences.class);
		Context context = new DefaultContext(1, 1);
		BlockContentManager m = new BlockContentManager(10, segments, rdp, refs, context, c);

		//test
		assertEquals(4, m.getRowCount());
		Iterator<RowImpl> i = m.iterator();
		assertEquals("⠀⠄⠄⠄⠀⠄⠄⠄", i.next().getChars());
		RowImpl r = i.next();
		assertEquals("⠀⠀⠀⠄⠄⠄", r.getLeftMargin().getContent()+r.getChars());
		r = i.next();
		assertEquals("⠀⠀⠀⠄⠄⠄", r.getLeftMargin().getContent()+r.getChars());
		r = i.next();
		assertEquals("⠀⠀⠀⠄⠄⠄", r.getLeftMargin().getContent()+r.getChars());
	}


}
