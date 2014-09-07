package deemo;

import java.awt.*;
import javax.swing.*;

public class Main extends JFrame{
  public Main(String name){
    super("Deemo");
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setSize(726,488);
	setResizable(false);
	Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((d.width-726)/2,(d.height-488)/2);
	setVisible(true);
	System.setProperty("sun.java2d.opengl","true");
	Graphics2D g=(Graphics2D)getContentPane().getGraphics();
	try{
	  new Compose(name,"hard",6).start(g);
	}catch(Exception e){}
  }
  public static void main(String args[]){
    new Main(args[0]);
  }
}