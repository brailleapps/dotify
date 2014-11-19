package org.daisy.dotify.devtools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.daisy.braille.embosser.Embosser;
import org.daisy.braille.embosser.EmbosserCatalogService;
import org.daisy.braille.table.TableCatalogService;
import org.daisy.factory.FactoryProperties;
import org.daisy.factory.FactoryPropertiesComparator;
import org.daisy.paper.Paper;
import org.daisy.paper.PaperCatalogService;
import org.osgi.framework.BundleContext;

public class EmbossPanel extends MyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051107255963928066L;
	private final JComboBox toptions;
	private final JComboBox poptions;
	private final JComboBox eoptions;

	private TableCatalogTracker tctracker;
	private PaperCatalogTracker pctracker;
	private EmbosserCatalogTracker ectracker;
	private int tfactoryIndex;
	private int pfactoryIndex;
	private int efactoryIndex;
	private final ActionListener tlistener;
	private final ActionListener plistener;
	private final ActionListener elistener;
	
	private Embosser embosser;

	public EmbossPanel() {
		setLayout(new GridLayout(2, 1));
		
		toptions = new JComboBox();		
		poptions = new JComboBox();
		eoptions = new JComboBox();
		
		tlistener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tfactoryIndex = toptions.getSelectedIndex();
			}
		};
		toptions.addActionListener(tlistener);
		
		plistener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pfactoryIndex = poptions.getSelectedIndex();
			}
		};
		poptions.addActionListener(plistener);
		
		elistener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				efactoryIndex = eoptions.getSelectedIndex();
				updateLists();
			}
		};
		eoptions.addActionListener(elistener);
		
		JPanel p = new JPanel(new BorderLayout());

		JPanel p2 = new JPanel(new GridLayout(3, 1));
		
		p2.add(eoptions);
		p2.add(toptions);
		p2.add(poptions);
		p.add(p2, BorderLayout.PAGE_START);
		add(p);

		setPreferredSize(new Dimension(500, 400));
		tfactoryIndex = 0;
		pfactoryIndex = 0;
		efactoryIndex = 0;
	}
	
	void updateLists() {
		updateEmbosserList();
		updateTableList();
		updatePaperList();
	}
	
	private void updateEmbosserList() {
		eoptions.removeActionListener(elistener);
		eoptions.removeAllItems();
		EmbosserCatalogService tt = ectracker.get();
		if (tt != null) {
			ArrayList<FactoryProperties> sorted = new ArrayList<FactoryProperties>(tt.list());
			Collections.sort(sorted, new FactoryPropertiesComparator());
			for (FactoryProperties p : sorted) {
				eoptions.addItem(new FactoryPropertiesItem(p));
			}
			try {
				eoptions.setSelectedIndex(efactoryIndex);
				embosser = tt.newEmbosser(((FactoryPropertiesItem)eoptions.getSelectedItem()).getFactoryProperties().getIdentifier());
			} catch (IllegalArgumentException e) {
				efactoryIndex = 0;
				embosser = null;
			}
		}
		eoptions.addActionListener(elistener);
	}
	
	private void updateTableList() {
		toptions.removeActionListener(tlistener);
		toptions.removeAllItems();
		TableCatalogService tt = tctracker.get();
		if (tt != null) {
			ArrayList<FactoryProperties> sorted;
			if (embosser!=null) {
				sorted = new ArrayList<FactoryProperties>(tt.list(embosser.getTableFilter()));
			} else {
				sorted = new ArrayList<FactoryProperties>(tt.list());
			}
			Collections.sort(sorted, new FactoryPropertiesComparator());
			for (FactoryProperties p : sorted) {
				toptions.addItem(new FactoryPropertiesItem(p));
			}
			try {
				toptions.setSelectedIndex(tfactoryIndex);
			} catch (IllegalArgumentException e) {
				tfactoryIndex = 0;
			}
		}
		toptions.addActionListener(tlistener);
	}
	
	private void updatePaperList() {
		poptions.removeActionListener(plistener);
		poptions.removeAllItems();
		PaperCatalogService tt = pctracker.get();
		if (tt != null) {
			ArrayList<Paper> sorted = new ArrayList<Paper>(tt.list());
			Collections.sort(sorted, new FactoryPropertiesComparator());
			for (Paper p : sorted) {
				if (embosser==null || embosser.supportsPaper(p)) {
					poptions.addItem(new FactoryPropertiesItem(p));
				}
			}
			try {
				poptions.setSelectedIndex(pfactoryIndex);
			} catch (IllegalArgumentException e) {
				pfactoryIndex = 0;
			}
		}
		poptions.addActionListener(plistener);
	}
	
	protected void updateResult() {

	}

	public void openTracking(BundleContext context) {
		tctracker = new TableCatalogTracker(context);
		tctracker.open();
		pctracker = new PaperCatalogTracker(context);
		pctracker.open();
		ectracker = new EmbosserCatalogTracker(context);
		ectracker.open();
	}

	public void closeTracking() {
		tctracker.close();
		pctracker.close();
		ectracker.close();
	}

}
