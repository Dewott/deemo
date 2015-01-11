package deemo;

import java.awt.*;
import java.util.*;

public class Note {
	Composition c = null;
	LinkedList<Sound> sounds = new LinkedList<Sound>();
	double x = 0;
	int time = 0;
	int id = 0;
	double size = 0;
	boolean link = false;
	boolean played = false;
	boolean showID = true;

	public Note(Composition c, int id, double x, int time, double size) {
		this.c = c;
		this.x = x;
		this.time = time;
		this.id = id;
		this.size = size;
	}

	public void addSound(Sound s) {
		sounds.add(s);
	}

	public void setLink(boolean link) {
		this.link = link;
	}

	public void paint(Graphics2D g) {
		if ((c.time >= time - Sound.latency) && (!played)) {
			played = true;
			for (Sound s : sounds)
				new Thread(s).start();
		}
		if (c.time >= time) {
			// ctime<=time+1000;
			double delta = (c.time - time) / 1000.0;
			double alpha = 1 - delta;
			double z = delta * 1 + 1;
			g.setColor(new Color(0, 1, 0, (float) alpha));
			for (Sound sound : sounds) {
				double x = (sound.pitch - 64) / 16.0;
				double v = 480 - 5 * z / (z + 4) * 80;
				double u = 4 * x / (z + 4) * 120 + 360;
				double s = 2 * size / (z + 4) * 30;
				g.drawLine((int) (u - s), (int) v, (int) (u + s), (int) v);
			}
			return;
		}

		if (Math.abs(x) > 2)
			return;
		double alpha = 1;
		double z = (time - c.time) / c.ar * 19 + 1;
		if (z >= 18)
			alpha = 1 - (z - 18) / 2.0;
		double v = 480 - 5 * z / (z + 4) * 80;
		double u = 4 * x / (z + 4) * 120 + 360;
		double s = 2 * size / (z + 4) * 120;

		if (link)
			g.setColor(new Color(1, 1, 0, (float) alpha));
		else if (sounds.size() > 0)
			g.setColor(new Color(0, 0, 0, (float) alpha));
		else
			g.setColor(new Color(0, 0, 1, (float) alpha));
		g.drawLine((int) (u - s), (int) v, (int) (u + s), (int) v);

		if (showID) {
			g.setColor(Color.RED);
			g.drawString(String.valueOf(id), (int) u, (int) v);
		}
	}
}
