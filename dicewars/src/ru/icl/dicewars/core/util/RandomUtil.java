package ru.icl.dicewars.core.util;

import java.util.Random;

public class RandomUtil {
	public static final Random rnd = new Random(System.currentTimeMillis());
	
	private RandomUtil() {
	}
	
	public static int getRandomInt(int n){
		return rnd.nextInt(n);
	}
}
