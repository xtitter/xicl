package ru.icl.dicewars.util;

import java.lang.reflect.Constructor;

public class ClassUtil {
	public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz,
			Class<?>... paramTypes) {
		if (clazz == null)
			throw new IllegalArgumentException();
		try {
			return clazz.getConstructor(paramTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
		if (lhsType == null || rhsType == null)
			throw new IllegalArgumentException();
		return lhsType.isAssignableFrom(rhsType);
	}

}
