package ru.intech.ussd.modeler.transformers;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;

import ru.intech.ussd.modeler.graphobjects.Vertex;

public class RoomShapeTransformer implements Transformer<Vertex,Shape> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private double width;
	private double height;
	private double scaleX;
	private double scaleY;
	private Rectangle2D shape;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public RoomShapeTransformer(double width, double height) {
		this.width = width;
		this.height = height;
		scaleX = scaleY = 1.0;
		shape = new Rectangle2D.Double(-width/2, -height/2, width, height);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public Shape transform(Vertex input) {
		return shape;
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
		setRect();
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		setRect();
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scalex) {
		this.scaleX = scalex;
		setRect();
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaley) {
		this.scaleY = scaley;
		setRect();
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public void setScale(double scale) {
		scaleX = scaleY = scale;
		setRect();
	}

	private void setRect() {
		double w = width * scaleX;
		double h = height * scaleY;
		shape.setRect(-w/2, -h/2, w, h);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
