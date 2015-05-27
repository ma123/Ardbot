package sk.kamil.gui;

import javax.swing.JFrame;

import sk.kamil.gui.control.MainPanel;

public class Main extends JFrame{
	private static final long serialVersionUID = -1371271281779247088L;
	private static String[] argsMain;
	public Main() {
	        this.setTitle("Kamil");
	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        MainPanel platno = new MainPanel(argsMain);
	        this.add(platno);
	        this.setResizable(true);
	        this.pack();
	    }
	    
	    public static void main(String[] args) {
	    	argsMain = args;
	    	Main hlavna = new Main();
		    hlavna.setVisible(true);
	    }
}
