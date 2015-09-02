package ru.intech.ussd.modeler.annotations;

public @interface UssdFunction {
	UssdFunctionArgument[] arguments();
	UssdFunctionResult[] results();
}
