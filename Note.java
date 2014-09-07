package deemo;
import java.awt.*;
import java.util.*;
public class Note{
  Compose c=null;
  LinkedList<Sound> sounds=new LinkedList<Sound>();
  double x=0;
  int time=0;
  double size=0;
  boolean link=false;
  boolean played=false;
  public Note(Compose c,double x,int time,double size){
    this.c=c;
	this.x=x;
	this.time=time;
	this.size=size;
  }
  public void addSound(Sound s){
    sounds.add(s);
  }
  public void setLink(boolean link){
    this.link=link;
  }
  public void paint(Graphics2D g){
    if((c.time>=time-Sound.latency)&&(!played)){
	  played=true;
	  for(Sound s:sounds) new Thread(s).start();
	}
	
	if(Math.abs(x)>2) return;
	
	double z=(time-c.time)/c.ar*19+1;
	double v=480-5*z/(z+4)*80;
	double u=4*x/(z+4)*120+360;
	double s=2*size/(z+4)*120;
	
	if(link) g.setColor(Color.YELLOW);
	else
	  if(sounds.size()>0) g.setColor(Color.BLACK);
	  else g.setColor(Color.BLUE);
	g.drawLine((int)(u-s),(int)v,(int)(u+s),(int)v);
	
  }
}
