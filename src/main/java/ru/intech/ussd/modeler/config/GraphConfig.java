package ru.intech.ussd.modeler.config;

import java.awt.Color;
import java.awt.Font;

import ru.intech.ussd.modeler.ui.X11Colors;

public class GraphConfig {

	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final double DEFAULT_SCALE_FACTOR = 1.25;

	private static final double DEFAULT_ROOM_WIDTH = 150;
	private static final double DEFAULT_ROOM_HEIGHT = 50;
	private static final Font DEFAULT_ROOM_FONT = new Font(null, Font.PLAIN, 10);

	private static final Color DEFAULT_COLOR_ROOM = new Color(X11Colors.SKY_BLUE);
	private static final Color DEFAULT_COLOR_START_ROOM = new Color(X11Colors.LIME_GREEN);
	private static final Color DEFAULT_COLOR_FINISH_ROOM = new Color(X11Colors.OLIVE_DRAB);

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private double scaleFactor;

	private double roomWidth;
	private double roomHeight;
	private Font roomFont;

	private Color colorRoom;
	private Color colorStartRoom;
	private Color colorFinishRoom;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public GraphConfig() {
		setDefaultScaleFactor();
		setDefaultRoomWidth();
		setDefaultRoomHeight();
		setDefaultRoomFont();
		setDefaultColorRoom();
		setDefaultColorStartRoom();
		setDefaultColorFinishRoom();
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public double getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public double getRoomWidth() {
		return roomWidth;
	}

	public void setRoomWidth(double roomWidth) {
		this.roomWidth = roomWidth;
	}

	public double getRoomHeight() {
		return roomHeight;
	}

	public void setRoomHeight(double roomHeight) {
		this.roomHeight = roomHeight;
	}

	public Font getRoomFont() {
		return roomFont;
	}

	public void setRoomFont(Font roomFont) {
		this.roomFont = roomFont;
	}

	public Color getColorRoom() {
		return colorRoom;
	}

	public void setColorRoom(Color colorRoom) {
		this.colorRoom = colorRoom;
	}

	public void setColorStartRoom(Color colorStartRoom) {
		this.colorStartRoom = colorStartRoom;
	}

	public Color getColorStartRoom() {
		return colorStartRoom;
	}

	public void setColorFinishRoom(Color colorFinishRoom) {
		this.colorFinishRoom = colorFinishRoom;
	}

	public Color getColorFinishRoom() {
		return colorFinishRoom;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public void setDefaultScaleFactor() {
		setScaleFactor(DEFAULT_SCALE_FACTOR);
	}

	public void setDefaultRoomWidth() {
		setRoomWidth(DEFAULT_ROOM_WIDTH);
	}

	public void setDefaultRoomHeight() {
		setRoomHeight(DEFAULT_ROOM_HEIGHT);
	}

	public void setDefaultRoomFont() {
		setRoomFont(DEFAULT_ROOM_FONT);
	}

	public void setDefaultColorRoom() {
		setColorRoom(DEFAULT_COLOR_ROOM);
	}

	public void setDefaultColorStartRoom() {
		setColorStartRoom(DEFAULT_COLOR_START_ROOM);
	}

	public void setDefaultColorFinishRoom() {
		setColorFinishRoom(DEFAULT_COLOR_FINISH_ROOM);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
