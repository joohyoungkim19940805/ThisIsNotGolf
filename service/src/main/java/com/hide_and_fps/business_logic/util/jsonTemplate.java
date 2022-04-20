package com.hide_and_fps.business_logic.util;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class jsonTemplate<K,V> extends ConcurrentHashMap<K,V>{
	
	public jsonTemplate(HashMap map) {
		super(map);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5405184309862895808L;
	
	
	public static void main(String args[]) {
		String test = " %s ";
		for(int i=0; i < 10;i++) {
			test += " %s ";
		}
		System.out.println(test);
		System.out.println(test.formatted("1","2","3","4","5","6","7","8","9","10","11"));
		String qqqq = null;
		System.out.println(qqqq.equals(""));
	}
}

