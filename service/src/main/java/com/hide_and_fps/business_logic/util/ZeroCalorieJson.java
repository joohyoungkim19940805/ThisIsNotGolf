package com.hide_and_fps.business_logic.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public ZeroCalorieJson(String str) {
		//this(str.replaceAll("[\n{}]", "").replaceAll("\\,", "\n").split("\n"));
	}
	public ZeroCalorieJson(String[] str) {
		System.out.println( Arrays.asList(str) );
		//Arrays.asList(str).parallelStream()
	}
	public ZeroCalorieJson(Entry<K,V> entry) {
		super.put(entry.getKey(), entry.getValue());
	}
	public ZeroCalorieJson(List<T> o) {
		destructuring( new ArrayList(o));
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
		return new Json((Map) super.clone(), 4).json;
	}
	
	public void setWhiteSpaceQuantity(int quantity) {
		this.whiteSpaceQuantity = quantity;
	}

	private class Json{
		public String json;
		public Json(Map map, int t) {
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
				obj = new Json((Map)obj, t).json;
			}else if(obj instanceof Entry<?, ?>) {
				obj = new Json( new ZeroCalorieJson( (Entry) obj ), t ).json;
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
	public static <T> void main(String args[]) {
		String test = " %s ";
		for(int i=0; i < 10;i++) {
			test += " %s ";
		}
		System.out.println(test);
		System.out.println(test.formatted("1","2","3","4","5","6","7","8","9","10","11"));

		Map test2 = Map.ofEntries(
					Map.entry("a", "1"),
					Map.entry("b", "2"),
					Map.entry("c", "3"),
					Map.entry("d", Map.ofEntries( Map.entry("e", "4"), Map.entry("f", Arrays.asList(1,2,3,4,Map.entry("g","5")) ), Map.entry("h","6"), Map.entry("i",Map.entry("j", "7"))) ),
					Map.entry("t", "qqq")
				);
		System.out.println(test2);
		
		ZeroCalorieJson json = new ZeroCalorieJson(test2);
		
		System.out.println(json);
		
		System.out.println(json.get("a"));
		
		ZeroCalorieJson json2 = new ZeroCalorieJson(Arrays.asList(1,2,3,4,5,6,7,8));
		System.out.println(json2);
		
		Map testMap = new HashMap();
		List testList = new ArrayList();
		for(int i = 1 ; i < 11 ; i++) {
			testList.add(i);
		}
		System.out.println("start!!<<");
		String testStr2 = new ZeroCalorieJson(testList).getJson();
		
		System.out.println(testStr2);
		System.out.println(json.getJson());
		System.out.println(json2.getJson());
		String []testStr4 = json.getJson().replaceAll("\n", "").split(":");
		System.out.println("==============");
		System.out.println(
				json.getJson().replaceAll("[\n{}]", "").replaceAll("(?<=[\\[]).*?(?=[]])", "")
				);
		System.out.println("==============");
		System.out.println(
				json.getJson().replaceAll("[\n{}]", "").replaceAll("\\,", "\n")
				);
		System.out.println(
				Arrays.asList( json.getJson().replaceAll("[\n{}]", "").replaceAll("\\,(?=([^\\[]*\\[[^\\]]*\\]))", "\n").split("\n") )
				);
		
		//new ZeroCalorieJson(json.getJson());
	}
}

