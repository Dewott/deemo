package deemo;

import net.sf.json.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.media.*;
//import javax.sound.midi.*;
import javazoom.jl.converter.*;

public class Composition {
	LinkedList<Note> notes = new LinkedList<Note>();
	String name = null;
	double ar = 600;
	int time = 0;

	public Composition(String name, String diff, int speed) throws Exception {
		this.name = name;
		new CompositionReader(name, diff);
		double t[] = new double[] { 8000, 4000, 2000, 1600, 1200, 900, 600,
				300, 100 };
		this.ar = t[speed - 1];
	}

	class CompositionReader {
		public CompositionReader(String name, String diff) throws Exception {
			BufferedReader r = new BufferedReader(new FileReader("TextAsset/"
					+ name + "." + diff + ".json.txt"));
			String str = "";
			String s = r.readLine();
			while (s != null) {
				str += s;
				s = r.readLine();
			}
			r.close();

			JSONObject obj = JSONObject.fromObject(str);
			JSONArray arr = JSONArray.fromObject(obj.get("notes"));
			for (Object tobj : arr) {
				JSONObject nobj = (JSONObject) tobj;
				int id = Integer.parseInt(nobj.getString("$id"));
				double x = nobj.optDouble("pos", 0);
				int time = (int) (nobj.optDouble("_time", 0) * 1000);
				double size = nobj.optDouble("size", 0);
				Note n = new Note(Composition.this, id, x, time, size);
				notes.add(n);

				JSONArray arr2 = nobj.optJSONArray("sounds");
				if (arr2 != null) {
					for (Object tobj2 : arr2) {
						JSONObject sobj = (JSONObject) tobj2;
						int d = (int) (sobj.getDouble("d") * 1000);
						int p = 64, v = 64;
						double w = 0;
						if (sobj.containsKey("p"))
							p = sobj.getInt("p");
						if (sobj.containsKey("v"))
							v = sobj.getInt("v");
						if (sobj.containsKey("w"))
							w = sobj.getDouble("w");
						n.addSound(new Sound(d, p, v, w));
					}
				}
			}

			arr = JSONArray.fromObject(obj.getString("links"));
			for (Object tobj : arr) {
				JSONObject link = (JSONObject) tobj;
				JSONArray arr2 = JSONArray.fromObject(link.getString("notes"));

				for (Object tobj2 : arr2) {
					JSONObject note = (JSONObject) tobj2;
					int p = Integer.parseInt(note.getString("$ref"));
					notes.get(p - 1).setLink(true);
				}
			}

			/*
			 * Sequence seq=new Sequence(Sequence.PPQ,1000);
			 * javax.sound.midi.Track t=seq.createTrack(); for(Note note:notes)
			 * for(Sound sound:note.sounds){ ShortMessage msg1=new
			 * ShortMessage();
			 * msg1.setMessage(ShortMessage.NOTE_ON,sound.pitch,sound.volume);
			 * long tick1=(long)(note.time*seq.getResolution()/500);
			 * ShortMessage msg2=new ShortMessage();
			 * msg2.setMessage(ShortMessage.NOTE_OFF,sound.pitch,sound.volume);
			 * long
			 * tick2=(long)((note.time+sound.duration)*seq.getResolution()/500);
			 * 
			 * t.add(new MidiEvent(msg1,tick1)); t.add(new
			 * MidiEvent(msg2,tick2)); } int[] fileTypes =
			 * MidiSystem.getMidiFileTypes(seq);
			 * MidiSystem.write(seq,fileTypes[0],new
			 * File("./midi/"+name+"."+diff+".midi"));
			 */
		}
	}

	public void start(Graphics2D g) {
		Player p = null;
		try {
			new Converter().convert("AudioClip/" + name + ".mp3", "temp.wav");
			p = Manager.createRealizedPlayer(new File("temp.wav").toURI()
					.toURL());
		} catch (Exception e) {
		}
		p.start();
		p.getGainControl().setLevel(0.2f);
		BufferedImage buf = new BufferedImage(720, 480,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gg = (Graphics2D) buf.getGraphics();
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		gg.setStroke(new BasicStroke(5));
		while (true) {
			gg.setColor(Color.WHITE);
			gg.fillRect(0, 0, 720, 480);
			gg.setColor(Color.BLACK);
			gg.drawLine(0, 400, 720, 400);
			time = (int) (p.getMediaTime().getSeconds() * 1000);
			LinkedList<Note> del = new LinkedList<Note>();
			for (Note n : notes) {
				if (time > n.time + 1000)
					del.add(n);
				else if ((time <= n.time + ar) && (time >= n.time - ar))
					n.paint(gg);
			}
			notes.removeAll(del);
			g.drawImage(buf, 0, 0, null);
			try {
				Thread.sleep(16);
			} catch (Exception e) {
			}
		}
	}
}