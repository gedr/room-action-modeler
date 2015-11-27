package ru.intech.ussd.modeler.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class FixedSizeDocument extends PlainDocument {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = -7706982703119692522L;

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private int maxLength = -1;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public FixedSizeDocument() {
		maxLength = -1;
	}

	public FixedSizeDocument(int max) {
		this.maxLength = max;
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		// check string being inserted does not exceed max length
		if ((maxLength > 0 && ((getLength() + str.length()) > maxLength))) {
			// If it does, then truncate it
			str = str.substring(0, maxLength - getLength());
		}
		super.insertString(offs, str, a);
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public void setUnlimitedMaxLength() {
		this.maxLength = -1;
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
