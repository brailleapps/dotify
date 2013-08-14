package org.daisy.dotify.devtools;
import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.daisy.dotify.devtools.views.BrailleConvertPanel;
import org.daisy.dotify.devtools.views.TextConvertPanel;


public class CodePointUtility extends javax.swing.JFrame {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3237476094594239863L;

	public CodePointUtility() {
		initComponents();
	}

    private void initComponents() {
        //Create and set up the window.
        setTitle("Code Point Utility");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TextConvertPanel textPanel = new TextConvertPanel();
        JComponent braillePanel = new BrailleConvertPanel(textPanel);

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(mainLayout);
        getContentPane().setBackground(Color.BLACK);
        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);
        JLabel textLabel = new JLabel("Text Input");
        textLabel.setForeground(Color.GRAY);
        JLabel brailleLabel = new JLabel("Braille Input");
        brailleLabel.setForeground(Color.GRAY);
        GroupLayout.SequentialGroup h2Group = mainLayout.createSequentialGroup();
        h2Group.addGroup(mainLayout.createParallelGroup()
        		.addComponent(brailleLabel)
        		.addComponent(braillePanel)
        		.addComponent(textLabel)
        		.addComponent(textPanel)

        		);
        mainLayout.setHorizontalGroup(h2Group);
        
        GroupLayout.SequentialGroup v2Group = mainLayout.createSequentialGroup();
        v2Group
        .addComponent(brailleLabel)
    	.addComponent(braillePanel)
        	.addComponent(textLabel)
        	.addComponent(textPanel)
        	;
        mainLayout.setVerticalGroup(v2Group);
        
        //Display the window.
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CodePointUtility().setVisible(true);
            }
        });
    }
    
}
