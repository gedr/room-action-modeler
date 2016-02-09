package ru.intech.ussd.modeler.transformers;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;

import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;

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
	private java.awt.geom.Ellipse2D specShape;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public RoomShapeTransformer(double width, double height) {
		this.width = width;
		this.height = height;
		scaleX = scaleY = 1.0;
		shape = new Rectangle2D.Double(-width/2, -height/2, width, height);
		double r = width < height ? width : height;
		specShape = new Ellipse2D.Double(-r / 2, -r / 2, r, r);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public Shape transform(Vertex input) {
		return input instanceof VertexSpecial ? specShape : shape;
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
		setShapes();
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		setShapes();
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scalex) {
		this.scaleX = scalex;
		setShapes();
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaley) {
		this.scaleY = scaley;
		setShapes();
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public void setScale(double scale) {
		scaleX = scaleY = scale;
		setShapes();
	}

	private void setShapes() {
		double w = width * scaleX;
		double h = height * scaleY;
		double r = w < h ? w : h;
		shape.setRect(-w/2, -h/2, w, h);
		specShape.setFrame(-r / 2, -r / 2, r, r);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
