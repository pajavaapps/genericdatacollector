package com.javaapps.gdc.model;

import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;

public class CamelCaseNamingStrategy extends PropertyNamingStrategy {
	@Override
	public String nameForGetterMethod(MapperConfig<?> config,
			AnnotatedMethod method, String defaultName) {
		return translate(defaultName);
	}

	@Override
	public String nameForSetterMethod(MapperConfig<?> config,
			AnnotatedMethod method, String defaultName) {
		return translate(defaultName);
	}

	@Override
	public String nameForField(MapperConfig<?> config, AnnotatedField field,
			String defaultName) {
		return translate(defaultName);
	}

	private String translate(String defaultName) {
		char[] nameChars = defaultName.toCharArray();
		StringBuilder nameTranslated = new StringBuilder(nameChars.length * 2);
		for (char c : nameChars) {
			if (Character.isUpperCase(c)) {
				nameTranslated.append("_");
				c = Character.toLowerCase(c);
			}
			nameTranslated.append(c);
		}
		return nameTranslated.toString();
	}
}