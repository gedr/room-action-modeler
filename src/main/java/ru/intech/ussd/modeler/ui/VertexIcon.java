package ru.intech.ussd.modeler.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import ru.intech.ussd.modeler.Main;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexFinish;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexStart;

public class VertexIcon implements Icon {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private BufferedImage bi ;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public VertexIcon(Vertex vertex, boolean picked) {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setDoubleBuffered(false);
		label.setSize((int) Math.round(Main.width), (int) Math.round(Main.height));

		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Main.fontSize));

		if (vertex instanceof VertexStart) {
			label.setText("start");
			label.setBackground(new Color(picked ?  X11Colors.LIME : X11Colors.LIME_GREEN));
			label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
		if (vertex instanceof VertexFinish) {
			label.setText("finish");
			label.setBackground(new Color(picked ?  X11Colors.OLIVE : X11Colors.OLIVE_DRAB));
			label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
		if (vertex instanceof VertexRoom) {
			Room room = ((VertexRoom) vertex).getRoom();
			String text = "<html><center>Room(" + (room == null ? "null" : String.valueOf(room.getId()))
					+ ")<br>" + ((VertexRoom) vertex).getDescription() + "</center></html>";
			label.setText(text);
			label.setBackground(new Color(picked ? X11Colors.DEEP_SKY_BLUE : X11Colors.SKY_BLUE));
			label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
		bi = new BufferedImage((int) Math.round(Main.width), (int) Math.round(Main.height), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		label.paint(g);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(bi, x, y, null);
	}

	public int getIconWidth() {
		return (int) Math.round(Main.width);
	}

	public int getIconHeight() {
		return (int) Math.round(Main.height);
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
