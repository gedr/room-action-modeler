package ru.intech.ussd.modeler.annotations;

public @interface UssdFunctionArgument {
	int order() default 1;
	String name();
	String defaultValue();
	String description();
}
