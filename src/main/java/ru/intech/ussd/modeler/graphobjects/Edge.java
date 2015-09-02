package ru.intech.ussd.modeler.graphobjects;

public interface Edge {
	boolean isChanged();
	void applyChanges();
}
