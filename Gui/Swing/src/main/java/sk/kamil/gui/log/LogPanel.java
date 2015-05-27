package sk.kamil.gui.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import sk.umb.projekty.ardbot.robot.Robot;

public class LogPanel extends JPanel{

	private static final long serialVersionUID = 4617726893168777108L;

	private Robot robot;

	private TextField inputTextField;
	private JTextArea outputTextArea;
	private String areaLog = "";
	private LogPanel panel = this;

    public LogPanel(Robot r) {
    	 this.robot = r;
    	 this.setBackground(new Color(112, 181, 155));
    	 this.setLayout(new BorderLayout());
    	 this.setBorder(new LineBorder(new Color(190, 190, 190), 2));
         this.setPreferredSize(new Dimension(220, 300));
         
         JPanel inputPanel = new JPanel();
         inputPanel.setLayout(new FlowLayout());
         this.add(inputPanel, BorderLayout.PAGE_START);
         
         inputTextField = new TextField(16);  // text field pre prikazy smerujuce na robota
         inputTextField.setBackground(new Color(112, 181, 155));
         inputPanel.add(inputTextField);
         
         JButton okButton = new JButton("OK");  // button na odosielanie prikazov 
         okButton.addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 			    String inputCommand = inputTextField.getText();
 			    areaLog += ">" + inputCommand + "\n";
 			    LogCommand logCommand = new LogCommand(robot, inputCommand, panel);
 			    outputTextArea.setText(areaLog);
 			}
 	     });
         okButton.setBackground(new Color(112, 181, 155));
         inputPanel.add(okButton);
         
         outputTextArea = new JTextArea(areaLog);
         outputTextArea.setEditable(false);
         outputTextArea.setBackground(new Color(112, 181, 155));
         this.add(outputTextArea, BorderLayout.CENTER);
             
         JScrollPane scrollOutputTextArea = new JScrollPane(outputTextArea);
         this.add(scrollOutputTextArea);
    }
    
    public void sendMessageArduino(String str) {
		   areaLog += "<" + str + "\n";
		   outputTextArea.setText(areaLog);
    }
}
