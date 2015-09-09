package ru.intech.ussd.modeler.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import ru.intech.ussd.modeler.config.GraphConfig;
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
	private double scale = 1.0;
	private GraphConfig config;
	private Vertex vertex;
	boolean picked;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public VertexIcon(Vertex vertex, boolean picked, GraphConfig config, double scale) {
		this.vertex = vertex;
		this.picked = picked;
		this.config = config;
		this.scale = scale;
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public void paintIcon(Component c, Graphics g, int x, int y) {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setDoubleBuffered(false);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		label.setSize(getIconWidth(), getIconHeight());
		label.setFont(config.getRoomFont().deriveFont((float) (config.getRoomFont().getSize() * scale)));

		if (vertex instanceof VertexStart) {
			label.setText(SpecialVertexName.start);
			label.setBackground(picked ? config.getColorPickedStartRoom() : config.getColorUnpickedStartRoom());
		}
		if (vertex instanceof VertexFinish) {
			label.setText(SpecialVertexName.finish);
			label.setBackground(picked ? config.getColorPickedFinishRoom() : config.getColorUnpickedFinishRoom());
		}
		if (vertex instanceof VertexRoom) {
			Room room = ((VertexRoom) vertex).getRoom();
			String text = "<html><center>Room(" + (room == null ? "null" : String.valueOf(room.getId()))
					+ ")<br/>" + ((VertexRoom) vertex).getDescription() + "</center></html>";
			label.setText(text);
			label.setBackground(picked ? config.getColorPickedRoom() : config.getColorUnpickedRoom());
		}
		BufferedImage bi = new BufferedImage(label.getWidth(), label.getHeight(), BufferedImage.TYPE_INT_ARGB);
		label.paint(bi.createGraphics());
		g.drawImage(bi, x, y, null);
	}

	public int getIconWidth() {
		return (int) Math.round(getConfig().getRoomWidth() * scale);
	}

	public int getIconHeight() {
		return (int) Math.round(getConfig().getRoomHeight() * scale);
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public GraphConfig getConfig() {
		if (config == null) {
			config = new GraphConfig();
		}
		return config;
	}

	public void setConfig(GraphConfig config) {
		this.config = config;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public boolean isPicked() {
		return picked;
	}

	public void setPicked(boolean picked) {
		this.picked = picked;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
