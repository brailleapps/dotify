package org.daisy.dotify.devtools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.daisy.braille.api.factory.FactoryProperties;
import org.daisy.braille.api.factory.FactoryPropertiesComparator;
import org.daisy.braille.api.table.BrailleConverter;
import org.daisy.braille.api.table.Table;
import org.daisy.braille.api.table.TableCatalogService;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.osgi.framework.BundleContext;

public class TranslatorPanel extends MyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051107255963928066L;
	private final JTextArea textField;
	private final JTextArea outputField;
	private final JComboBox options;
	private final ActionListener listener;

	private TranslatorTracker tracker;
	private TableCatalogTracker tctracker;
	private BrailleConverter bc;
	private int factoryIndex;
	
	
	public TranslatorPanel() {
		setLayout(new GridLayout(2, 1));

		Font f = new Font(null, Font.BOLD, 26);

		outputField = new JTextArea();

		try {
			InputStream fs = this.getClass().getResourceAsStream("resource-files/odt2braille6.ttf");
			Font font = Font.createFont(Font.TRUETYPE_FONT, fs);
			fs.close();
			outputField.setFont(font.deriveFont(24f));
		} catch (IOException e) {
			e.printStackTrace();
			outputField.setFont(f);
		} catch (FontFormatException e1) {
			e1.printStackTrace();
			outputField.setFont(f);
		}
        
		outputField.setEditable(false);

		textField = new JTextArea();
		textField.setFont(f);

		textField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateResult();
			}

		});
		
		options = new JComboBox();
		listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FactoryProperties tableFactory = ((FactoryPropertiesItem)options.getSelectedItem()).getFactoryProperties();
				bc = newConverter(tableFactory);
				factoryIndex = options.getSelectedIndex();
				updateResult();
			}
		};

		options.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				updateTableList();
			}
		});
		
		JPanel p = new JPanel(new BorderLayout());

		add(textField);
		p.add(outputField);
		p.add(options,BorderLayout.PAGE_START);
		add(p);

		setPreferredSize(new Dimension(500, 400));
		factoryIndex = 0;
	}
	
	void updateTableList() {
		options.removeActionListener(listener);
		options.removeAllItems();
		TableCatalogService tt = tctracker.get();
		if (tt != null) {
			ArrayList<FactoryProperties> sorted = new ArrayList<FactoryProperties>(tt.list());
			Collections.sort(sorted, new FactoryPropertiesComparator());
			for (FactoryProperties p : sorted) {
				options.addItem(new FactoryPropertiesItem(p));
			}
			try {
				options.setSelectedIndex(factoryIndex);
			} catch (IllegalArgumentException e) {
				factoryIndex = 0;
			}
		}
		options.addActionListener(listener);
	}
	
	@Override
	protected void updateResult() {
		String loc = getTargetLocale();
		if (loc==null || loc.equals("")) {
			outputField.setText("No locale selected");
		} else {
			BrailleTranslatorFactoryMakerService t = tracker.get();
			if (t == null) {
				outputField.setText("No conversion");
			} else {
				if (textField.getText().equals("")) {
					outputField.setText("");
				} else {
					try {
						outputField.setText("");
						BrailleTranslatorResult btr = t.newTranslator(loc, BrailleTranslatorFactory.MODE_UNCONTRACTED).translate(textField.getText());
						while (btr.hasNext()) {
							String str = btr.nextTranslatedRow(30, true);
							if (bc!=null) {
								str = bc.toText(str);
							}
							outputField.append(str + "\n");
						}

					} catch (TranslatorConfigurationException e1) {
						outputField.setText("Specification not supported. " + tracker.size());
					}
				}
			}
		}
	}
	
	private BrailleConverter newConverter(FactoryProperties fpp) {
		TableCatalogService tt = tctracker.get();
		if (tt != null) {
			if (fpp==null) {
				Collection<FactoryProperties> fp = tt.list();
				if (fp.size()>0) {
					fpp = fp.iterator().next();									
				}
			}
			if (fpp!=null) {
				Table ttt = tt.newTable(fpp.getIdentifier());
				return ttt.newBrailleConverter();
			}							
		}
		return null;
	}

	@Override
	public void openTracking(BundleContext context) {
		tracker = new TranslatorTracker(context);
		tracker.open();
		tctracker = new TableCatalogTracker(context);
		tctracker.open();
	}

	@Override
	public void closeTracking() {
		tracker.close();
		tctracker.close();
	}

}
