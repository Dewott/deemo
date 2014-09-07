package deemo;

import net.sf.json.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.media.*;
import javazoom.jl.converter.*;

public class Compose{
  LinkedList<Note> notes=new LinkedList<Note>();
  String name=null;
  double ar=600;
  int time=0;
  public Compose(String name,String diff,int speed) throws Exception{
    this.name=name;
    new ComposeReader(name,diff);
	double t[]=new double[]{8000,4000,2000,1600,1200,900,600,300,100};
	this.ar=t[speed-1];
  }
  class ComposeReader{
    public ComposeReader(String name,String diff) throws Exception{
	  BufferedReader r=new BufferedReader(new FileReader("TextAsset/"+name+"."+diff+".json.txt"));
	  String str="";
	  String s=r.readLine();
	  while(s!=null){
	    str+=s;
		s=r.readLine();
	  }
	  r.close();
	  
	  JSONObject obj=JSONObject.fromObject(str);
	  JSONArray arr=JSONArray.fromObject(obj.get("notes"));
	  for(Object tobj:arr){
	    JSONObject nobj=JSONObject.fromObject(tobj);
		double x=nobj.optDouble("pos",0);
		int time=(int)(nobj.getDouble("_time")*1000);
		double size=nobj.getDouble("size");
		Note n=new Note(Compose.this,x,time,size);
		notes.add(n);
		
		JSONArray arr2=nobj.optJSONArray("sounds");
		if(arr2!=null){
		  for(Object tobj2:arr2){
		    JSONObject sobj=JSONObject.fromObject(tobj2);
			int d=(int)(sobj.getDouble("d")*1000);
			int p=sobj.getInt("p");
			int v=sobj.getInt("v");
			double w=sobj.optDouble("w",0);
			n.addSound(new Sound(d,p,v,w));
		  }
		}
	  }
	  
	  arr=JSONArray.fromObject(obj.get("links"));
	  for(Object tobj:arr){
	    JSONObject link=JSONObject.fromObject(tobj);
		JSONArray arr2=JSONArray.fromObject(link.get("notes"));
		
		for(Object tobj2:arr2){
		  JSONObject note=JSONObject.fromObject(tobj2);
		  int p=Integer.parseInt(note.getString("$ref"));
		  notes.get(p-1).setLink(true);
		}
	  }
	}
  }
  public void start(Graphics2D g){
    Player p=null;
    try{
	  new Converter().convert("AudioClip/"+name+".mp3","temp.wav");
      p=Manager.createRealizedPlayer(new File("temp.wav").toURI().toURL());
	}catch(Exception e){}
    p.start();
	p.getGainControl().setLevel(0.4f);
	BufferedImage buf=new BufferedImage(720,480,BufferedImage.TYPE_INT_ARGB);
	Graphics2D gg=(Graphics2D)buf.getGraphics();
	gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	gg.setStroke(new BasicStroke(5));
	while(true){
	  gg.setColor(Color.WHITE);
	  gg.fillRect(0,0,720,480);
	  gg.setColor(Color.BLACK);
	  gg.drawLine(0,400,720,400);
	  time=(int)(p.getMediaTime().getSeconds()*1000);
	  LinkedList<Note> del=new LinkedList<Note>();
	  for(Note n:notes){
	    if(time>n.time) del.add(n);
		else
	      if((time<=n.time+ar)&&(time>=n.time-ar)) n.paint(gg);
	  }
	  notes.removeAll(del);
	  g.drawImage(buf,0,0,null);
	  try{
	    Thread.sleep(10);
	  }catch(Exception e){}
	}
  }
}