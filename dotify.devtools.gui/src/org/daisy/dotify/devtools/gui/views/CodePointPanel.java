package org.daisy.dotify.devtools.gui.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.daisy.dotify.devtools.converters.CodePointHelper;
import org.daisy.dotify.devtools.converters.CodePointHelper.Mode;
import org.daisy.dotify.devtools.converters.CodePointHelper.Style;
import org.daisy.dotify.devtools.converters.UnicodeNames;

public class CodePointPanel extends JPanel implements StringFormatterResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2155201906265896180L;
	private final static String xmlStr = "XML entity";
	private final static String commaStr = "Comma separated";
	private final static String hexStr = "Hex";
	private final static String decimalStr = "Decimal";

	JRadioButton s1, s2, s3, m1, m2;// , i1, i2;
	
	JTextArea codeTextField;
	// JLabel inputType;
	JScrollPane codePanelScroll;
	JList list;
	// Create the listener list
    protected javax.swing.event.EventListenerList listenerList =
        new javax.swing.event.EventListenerList();

	public CodePointPanel() {
		list = new JList();
		// JPanel input = new JPanel();
        JPanel style = new JPanel();
        JPanel mode = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
		ButtonGroup mButtonGroup = new ButtonGroup();
		// ButtonGroup iButtonGroup = new ButtonGroup();
		ActionListener updateStyle = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				styleChanged();
			}
		};
		
		ActionListener updateMode = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modeChanged();
			}
		};
		/*
		 * ActionListener inputMode = new ActionListener() {
		 * @Override
		 * public void actionPerformed(ActionEvent e) {
		 * inputChanged();
		 * }
		 * };
		 */

		s1 = new JRadioButton(xmlStr);
		s2 = new JRadioButton(commaStr);
		s3 = new JRadioButton("Names list (readonly)");

		s1.addActionListener(updateStyle);
		s2.addActionListener(updateStyle);
		s3.addActionListener(updateStyle);

		m1 = new JRadioButton(hexStr);
		m2 = new JRadioButton(decimalStr);
		m1.addActionListener(updateMode);
		m2.addActionListener(updateMode);
		
		/*
		 * i1 = new JRadioButton("Code points");
		 * i2 = new JRadioButton("Names");
		 * i1.addActionListener(inputMode);
		 * i2.addActionListener(inputMode);
		 */

		buttonGroup.add(s1);
		buttonGroup.add(s2);
		buttonGroup.add(s3);
		
		mButtonGroup.add(m1);
		mButtonGroup.add(m2);

		/*
		 * iButtonGroup.add(i1);
		 * iButtonGroup.add(i2);
		 */

		s2.setSelected(true);
		m1.setSelected(true);
		// i1.setSelected(true);
		
		style.setLayout(new GridLayout(3, 1));
		style.add(s1);
		style.add(s2);
		style.add(s3);
		
		mode.setLayout(new GridLayout(3, 1));
		mode.add(m1);
		mode.add(m2);
		
		/*
		 * input.setLayout(new GridLayout(1, 2));
		 * input.add(i1);
		 * input.add(i2);
		 */

		codeTextField = new JTextArea(10, 30);

        codeTextField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==10) {
					fireTextChangedEvent(new TextChangedEvent(this));
				}
			}
			});
        
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		setLayout(layout);
		// getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);



		// inputType = new JLabel("tttt");
		codePanelScroll = new JScrollPane(codeTextField);
        JPanel options = new JPanel(new GridLayout(1, 2));
        options.add(style);
        options.add(mode);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup(Alignment.LEADING)
		// .addComponent(input)
		.addComponent(codePanelScroll).addComponent(options));
		layout.setHorizontalGroup(hGroup);
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		// The sequential group contains two parallel groups that align
		// the contents along the baseline. The first parallel group contains
		// the first label and text field, and the second parallel group
		// contains
		// the second label and text field. By using a sequential group
		// the labels and text fields are positioned vertically after one
		// another.
		vGroup.
		// addComponent(input).
		addComponent(codePanelScroll).addComponent(options);

		layout.setVerticalGroup(vGroup);
	}
	
	public void addTextChangedListener(TextChangedListener listener) {
		listenerList.add(TextChangedListener.class, listener);
	}
	
	public void removeTextChangedListener(TextChangedListener listener) {
		listenerList.remove(TextChangedListener.class, listener);
	}
	
	@Override
	public void setResult(String text) {
		String input;
		/*
		 * try {
		 * input = BrailleNotationConverter.parsePNotation(text);
		 * inputType.setText("P-notation:");
		 * } catch (IllegalArgumentException e) {
		 */
			input = text;
		// inputType.setText("Characters:");
		// }
		updateCharacterNames(input);
		codeTextField.setText(CodePointHelper.format(input, getSelectedStyle(), getSelectedMode()));
	}
	
	protected void fireTextChangedEvent(TextChangedEvent evt) {
		Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
		for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==TextChangedListener.class) {
                ((TextChangedListener)listeners[i+1]).textChanged(evt);
            }
		}
	}

	@Override
	public String getResultAsText() {
		// inputType.setText("Characters:");
		return CodePointHelper.parse(codeTextField.getText(), getSelectedMode());
	}
	
	private void updateCharacterNames(String input) {
		int codePoint;
		ArrayList<String> ret = new ArrayList<String>();
		for (int offset = 0; offset < input.length();) {
			codePoint = input.codePointAt(offset);
			ret.add(UnicodeNames.getName(codePoint) + ", " + CodePointHelper.format("" + (char) codePoint, Style.COMMA, getSelectedMode()));
			offset += Character.charCount(codePoint);
		}
		list.setListData(ret.toArray(new String[] {}));
	}

	private void styleChanged() {
		switch (getSelectedStyle()) {
			case XML:
				setResult(CodePointHelper.parse(codeTextField.getText(), getSelectedMode()));
				break;
			case COMMA:
				setResult(CodePointHelper.parse(codeTextField.getText(), getSelectedMode()));
				break;
		}
		if (s3.isSelected()) {
			codePanelScroll.setViewportView(list);
		} else {
			codePanelScroll.setViewportView(codeTextField);
		}
	}
	private void modeChanged() {
		switch (getSelectedMode()) {
			case HEX:
				setResult(CodePointHelper.parse(codeTextField.getText(), Mode.DECIMAL));
				break;
			case DECIMAL:
				setResult(CodePointHelper.parse(codeTextField.getText(), Mode.HEX));
				break;
		}
		
	}

	/*
	 * private void inputChanged() {
	 * switch (getSelectedInput()) {
	 * case NAME:
	 * System.out.println("Name");
	 * codePanelScroll.setViewportView(list);
	 * // setResult(CodePointHelper.parse(codeTextField.getText(),
	 * // Mode.DECIMAL));
	 * break;
	 * case CODE:
	 * System.out.println("code");
	 * codePanelScroll.setViewportView(codeTextField);
	 * // setResult(CodePointHelper.parse(codeTextField.getText(),
	 * // Mode.HEX));
	 * break;
	 * }
	 * }
	 */
	private Style getSelectedStyle() {
		if (s1.isSelected()) {
			return Style.XML;
		} else if (s2.isSelected()) {
			return Style.COMMA;
		}
		return Style.COMMA;
	}
	private Mode getSelectedMode() {
		if (m1.isSelected()) {
			return Mode.HEX;
		} else if (m2.isSelected()) {
			return Mode.DECIMAL;
		}
		return Mode.HEX;
	}
	/*
	 * private Input getSelectedInput() {
	 * if (i1.isSelected()) {
	 * return Input.CODE;
	 * } else if (i2.isSelected()) {
	 * return Input.NAME;
	 * }
	 * return Input.CODE;
	 * }
	 */

}
