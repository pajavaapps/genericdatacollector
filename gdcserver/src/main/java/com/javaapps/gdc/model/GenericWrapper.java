package com.javaapps.gdc.model;

public class GenericWrapper implements Comparable {
	private String key;
	private String value;

	public GenericWrapper(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "GenericWrapper [key=" + key + ", value=" + value + "]";
	}

	public int compareTo(Object obj) {
		GenericWrapper wrapper2 = (GenericWrapper) obj;
		if (wrapper2 == null) {
			return 1;
		} else if (wrapper2.getKey() == null) {
			return 1;
		} else if (key == null) {
			return -1;
		} else {
			return key.compareTo(wrapper2.getKey());
		}
	}

}
