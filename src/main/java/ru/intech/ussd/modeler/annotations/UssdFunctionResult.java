package ru.intech.ussd.modeler.annotations;

public @interface UssdFunctionResult {
	char key() default '-';
	String description();
}
