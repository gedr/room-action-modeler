package ru.intech.ussd.modeler.util;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalMulti<T> {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final AtomicInteger ATOM_INT = new AtomicInteger(0);
	private static final ThreadLocal<Map<Integer, Object>> LOCAL = new ThreadLocal<Map<Integer, Object>>();

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Integer instanceCode;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public ThreadLocalMulti() {
		instanceCode = Integer.valueOf(ATOM_INT.incrementAndGet());
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	@SuppressWarnings("unchecked")
	public T get() {
		Map<Integer, Object> map = LOCAL.get();
		return (T) (((map == null) || map.isEmpty()) ? null : map.get(instanceCode));
	}

	public void set(T val) {
		if (val == null) {
			remove();
			return;
		}
		Map<Integer, Object> map = LOCAL.get();
		if (map == null) {
			map = new TreeMap<Integer, Object>();
		}
		map.put(instanceCode, val);
		LOCAL.set(map);
	}

	public void remove() {
		Map<Integer, Object> map = LOCAL.get();
		if ((map != null) && !map.isEmpty()) {
			map.remove(instanceCode);
		}
		if ((map == null) || map.isEmpty()) {
			LOCAL.remove();
		}
	}
}