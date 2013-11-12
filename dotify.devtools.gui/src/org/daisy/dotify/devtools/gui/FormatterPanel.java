package org.daisy.dotify.devtools.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.daisy.dotify.api.engine.FormatterEngine;
import org.daisy.dotify.api.engine.FormatterEngineFactoryService;
import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.writer.PEFMediaWriter;
import org.osgi.framework.BundleContext;

public class FormatterPanel extends MyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051107255963928066L;
	private final JButton chooseFile;
	private final JFileChooser chooser;
	private final JTextArea outputField;
	private File input;

	private FormatterTracker tracker;
	
	public FormatterPanel() {
		super(new GridLayout(2, 1));
		
		input = null;

		Font f = new Font(null, Font.BOLD, 26);

		outputField = new JTextArea();
		outputField.setFont(f);
		outputField.setEditable(false);
		
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "OBFL-files", "obfl");

		chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		
		chooseFile = new JButton("Choose file...");
		chooseFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal =chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       input = chooser.getSelectedFile();
			       chooseFile.setText(chooser.getSelectedFile().getName());
			       updateResult();
			    }
			}});

		add(chooseFile);
		add(new JScrollPane(outputField));

		setPreferredSize(new Dimension(500, 400));
	}

	protected void updateResult() {
		if (getTargetLocale()==null || getTargetLocale().equals("")) {
			outputField.setText("No locale selected");
		} else {

				FormatterEngineFactoryService t = tracker.get();
				if (t == null) {
					outputField.setText("No formatter detected");
				} else {
					if (input==null) {
						outputField.setText("No file selected.");
						chooseFile.setText("Choose file...");
					} else if (!input.isFile()) { 
						outputField.setText("File doesn't exist " + input);
					} else {
						Properties p = new Properties();
						FormatterEngine e = t.newFormatterEngine(getTargetLocale(), BrailleTranslatorFactory.MODE_UNCONTRACTED, new PEFMediaWriter(p));
						File out = new File(input.getParentFile(), input.getName()+".pef");
						try {
							e.convert(new FileInputStream(input), new FileOutputStream(out));
							outputField.setText("Done! " + out);
						} catch (FileNotFoundException e1) {
							outputField.setText(e1.toString());
						} catch (LayoutEngineException e1) {
							outputField.setText("Not supported");
						}
					}
				}

		}
	}

	public void openTracking(BundleContext context) {
		tracker = new FormatterTracker(context);
		tracker.open();
	}

	public void closeTracking() {
		tracker.close();
	}

}
