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

	private static final Color DEFAULT_COLOR_PICKED_ROOM = new Color(X11Colors.DEEP_SKY_BLUE);
	private static final Color DEFAULT_COLOR_UNPICKED_ROOM = new Color(X11Colors.SKY_BLUE);
	private static final Color DEFAULT_COLOR_PICKED_START_ROOM = new Color(X11Colors.LIME);
	private static final Color DEFAULT_COLOR_UNPICKED_START_ROOM = new Color(X11Colors.LIME_GREEN);
	private static final Color DEFAULT_COLOR_PICKED_FINISH_ROOM = new Color(X11Colors.OLIVE);
	private static final Color DEFAULT_COLOR_UNPICKED_FINISH_ROOM = new Color(X11Colors.OLIVE_DRAB);

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private double scaleFactor;

	private double roomWidth;
	private double roomHeight;
	private Font roomFont;

	private Color colorPickedRoom;
	private Color colorUnpickedRoom;
	private Color colorPickedStartRoom;
	private Color colorUnpickedStartRoom;
	private Color colorPickedFinishRoom;
	private Color colorUnpickedFinishRoom;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public GraphConfig() {
		setDefaultScaleFactor();
		setDefaultRoomWidth();
		setDefaultRoomHeight();
		setDefaultRoomFont();
		setDefaultColorPickedRoom();
		setDefaultColorUnpickedRoom();
		setDefaultColorPickedStartRoom();
		setDefaultColorUnpickedStartRoom();
		setDefaultColorPickedFinishRoom();
		setDefaultColorUnpickedFinishRoom();
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

	public Color getColorPickedRoom() {
		return colorPickedRoom;
	}

	public void setColorPickedRoom(Color colorPickedRoom) {
		this.colorPickedRoom = colorPickedRoom;
	}

	public Color getColorUnpickedRoom() {
		return colorUnpickedRoom;
	}

	public void setColorUnpickedRoom(Color colorUnpickedRoom) {
		this.colorUnpickedRoom = colorUnpickedRoom;
	}

	public Color getColorPickedStartRoom() {
		return colorPickedStartRoom;
	}

	public void setColorPickedStartRoom(Color colorPickedStartRoom) {
		this.colorPickedStartRoom = colorPickedStartRoom;
	}

	public Color getColorUnpickedStartRoom() {
		return colorUnpickedStartRoom;
	}

	public void setColorUnpickedStartRoom(Color colorUnpickedStartRoom) {
		this.colorUnpickedStartRoom = colorUnpickedStartRoom;
	}

	public Color getColorPickedFinishRoom() {
		return colorPickedFinishRoom;
	}

	public void setColorPickedFinishRoom(Color colorPickedFinishRoom) {
		this.colorPickedFinishRoom = colorPickedFinishRoom;
	}

	public Color getColorUnpickedFinishRoom() {
		return colorUnpickedFinishRoom;
	}

	public void setColorUnpickedFinishRoom(Color colorUnpickedFinishRoom) {
		this.colorUnpickedFinishRoom = colorUnpickedFinishRoom;
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

	public void setDefaultColorPickedRoom() {
		setColorPickedRoom(DEFAULT_COLOR_PICKED_ROOM);
	}

	public void setDefaultColorUnpickedRoom() {
		setColorUnpickedRoom(DEFAULT_COLOR_UNPICKED_ROOM);
	}

	public void setDefaultColorPickedStartRoom() {
		setColorPickedStartRoom(DEFAULT_COLOR_PICKED_START_ROOM);
	}

	public void setDefaultColorUnpickedStartRoom() {
		setColorUnpickedStartRoom(DEFAULT_COLOR_UNPICKED_START_ROOM);
	}

	public void setDefaultColorPickedFinishRoom() {
		setColorPickedFinishRoom(DEFAULT_COLOR_PICKED_FINISH_ROOM);
	}

	public void setDefaultColorUnpickedFinishRoom() {
		setColorUnpickedFinishRoom(DEFAULT_COLOR_UNPICKED_FINISH_ROOM);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
