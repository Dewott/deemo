package deemo;
import javax.sound.midi.*;
//-classpath rt.jar
public class Sound implements Runnable{
  int duration=0;
  int pitch=0;
  int volume=0;
  double wane=0;
  static MidiChannel c=null;
  static long latency=0;
  static{
    try{
      Synthesizer s=MidiSystem.getSynthesizer();
	  latency=s.getLatency()/1000;
      Instrument ins[]=s.getAvailableInstruments();
	  s.loadInstrument(ins[0]);
	  s.open();
	  c=s.getChannels()[0];
	}catch(Exception e){}
  }
  public Sound(int duration,int pitch,int volume,double wane){
    this.duration=duration;
	this.pitch=pitch;
	this.volume=volume;
	this.wane=wane;
  }
  public void run(){
    c.noteOn(pitch,volume);
	try{
	  Thread.sleep(duration);
	}catch(Exception e){}
	c.noteOff(pitch,0);
  }
}