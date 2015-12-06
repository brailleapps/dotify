package org.daisy.dotify.devtools.gui.views;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.daisy.dotify.common.braille.BrailleNotationConverter;


public class BrailleConvertPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2446355314544241740L;
	JLabel label;
	JTextField input;
	final StringFormatterResult tr;
	private final BrailleNotationConverter bnc;
	
	public BrailleConvertPanel(StringFormatterResult tr) {
		this.tr = tr;
		this.bnc = new BrailleNotationConverter("p");
        label = new JLabel();
        label.setText("Braille (p-notation)");
        input = new JTextField(30);
        input.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==10) {
					buttonActionPerformed();
				}
			}
			});
        JButton button = new JButton();
        button.setText("To Code Points ↓");
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed();
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        //getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
      
        // The sequential group in turn contains two parallel groups.
        // One parallel group contains the labels, the other the text fields.
        // Putting the labels in a parallel group along the horizontal axis
        // positions them at the same x location.
        //
        // Variable indentation is used to reinforce the level of grouping.
        hGroup.addGroup(layout.createParallelGroup().
                 addComponent(label));
        hGroup.addGroup(layout.createParallelGroup().
                 addComponent(input).addComponent(button));
        layout.setHorizontalGroup(hGroup);
        
        // Create a sequential group for the vertical axis.
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
      
        // The sequential group contains two parallel groups that align
        // the contents along the baseline. The first parallel group contains
        // the first label and text field, and the second parallel group contains
        // the second label and text field. By using a sequential group
        // the labels and text fields are positioned vertically after one another.
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
                 addComponent(label).addComponent(input));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
                 addComponent(button));
        layout.setVerticalGroup(vGroup);
	}
	
    private void buttonActionPerformed() {
		try {
			tr.setResult(bnc.parseBrailleNotation(input.getText()));
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
    }

}
