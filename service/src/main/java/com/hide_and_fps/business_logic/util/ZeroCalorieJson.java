package com.hide_and_fps.business_logic.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ZeroCalorieJson<K,V, T> extends HashMap<K,V>{
	
	private static final long serialVersionUID = 5405184309862895808L;
	private int whiteSpaceQuantity = 4;

	public ZeroCalorieJson(){
		super();
	}
	public ZeroCalorieJson(HashMap hashMap) {
		super(hashMap);
	}
	public ZeroCalorieJson(Map map) {
		super(map);
	}
	@SafeVarargs
	public ZeroCalorieJson(Entry<K,V> ... entrys) {
		this(Map.ofEntries(entrys));
	}
	public ZeroCalorieJson(Entry<K,V> entry) {
		super.put(entry.getKey(), entry.getValue());
	}
	public ZeroCalorieJson setListIndexKeyAndValue(List<T> o) {
		destructuring( new ArrayList(o));
		return this;
	}
	private void destructuring(ArrayList objects) {
		if(objects.size() %2 != 0) {
			throw new ArrayIndexOutOfBoundsException(" List Size % 2 == Not True");
		}else {
			ArrayList oList = (ArrayList) objects.clone();
			oList.stream().forEach(item -> {
				if(objects.size() > 1 && objects.get(0) != null && objects.get(1) != null) {
					K key = (K) objects.get(0).toString();
					V value = (V) objects.get(1);
					super.put(key, value);
					objects.remove(0);
					objects.remove(0);
				}
			});
		}
	}
	
	public String getJson() {
		return new ZeroSon((Map) super.clone(), whiteSpaceQuantity).json;
	}
	
	public void setWhiteSpaceQuantity(int quantity) {
		this.whiteSpaceQuantity = quantity;
	}

	private class ZeroSon{
		public String json;
		public ZeroSon(Map map, int t) {
			this.json = """
					{
					%s
					}"""
					.formatted( String.join(",\n",
											 map.entrySet()
											.parallelStream()
											.map(entry ->{
													String key = ((Entry<String, Object>) entry).getKey().toString();
													Object value = ((Entry<String, Object>) entry).getValue();
													value = convert(value, t);
													return """
													"%s" : %s """.formatted(key, value.toString());
											}).toList()
										).indent(t)
								);
			
		}
		
		private Object convert(Object obj, int t) {
			if(obj instanceof Map<?,?>){
				obj = new ZeroSon((Map)obj, t).json;
			}else if(obj instanceof Entry<?, ?>) {
				obj = new ZeroSon( new ZeroCalorieJson( (Entry) obj ), t ).json;
			}else if(obj instanceof List<?>) {
				obj = ((List) obj).parallelStream().map(item -> {
					item = convert(item, t);
					return item;
				}).toList();
			}else {
				obj = "\""+obj+"\"";
			}

			return obj;
		}
	}
	
	public static void main(String args[]) {
		System.out.println(new Date().getTime());
	}
}

