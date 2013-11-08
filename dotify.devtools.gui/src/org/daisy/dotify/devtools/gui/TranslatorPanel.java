package org.daisy.dotify.devtools.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JTextField;

import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.osgi.framework.BundleContext;

public class TranslatorPanel extends MyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051107255963928066L;
	private final JTextField textField;
	private final JTextField outputField;

	private TranslatorTracker tracker;
	
	public TranslatorPanel() {
		setLayout(new GridLayout(2, 1));

		Font f = new Font(null, Font.BOLD, 26);

		outputField = new JTextField();

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

		textField = new JTextField();
		textField.setFont(f);

		textField.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				updateResult();
			}

		});

		add(textField);
		add(outputField);

		setPreferredSize(new Dimension(500, 400));
	}
	
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

						outputField.setText(
								t.newTranslator(loc, BrailleTranslatorFactory.MODE_UNCONTRACTED).translate(textField.getText()).getTranslatedRemainder()
								);
					} catch (TranslatorConfigurationException e1) {
						outputField.setText("Specification not supported. " + tracker.size());
					}
				}
			}
		}
	}

	public void openTracking(BundleContext context) {
		tracker = new TranslatorTracker(context);
		tracker.open();
	}

	public void closeTracking() {
		tracker.close();
	}

}
